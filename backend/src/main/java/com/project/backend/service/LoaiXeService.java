package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO; // <-- Import
import com.project.backend.dto.LoaiXeRequestDTO; // <-- Import
import com.project.backend.dto.PhanBoLoaiXeDTO;
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.LoaiXe;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // <-- Import


@Service
public class LoaiXeService {

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<LoaiXeDTO> getAllLoaiXe() {
        // 1. Lấy List<Entity>
        List<LoaiXe> danhSachEntity = loaiXeRepository.findAll();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper
                .collect(Collectors.toList());
    }

    public LoaiXeDTO getLoaiXeById(String id) {
        // 1. Lấy Entity
        LoaiXe loaiXeEntity = timLoaiXeBangId(id);
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(loaiXeEntity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public LoaiXeDTO createLoaiXe(LoaiXeRequestDTO dto) {
        // 1. Chuyển DTO -> Entity
        LoaiXe loaiXeMoi = new LoaiXe();
        loaiXeMoi.setTenLoai(dto.getTenLoai());

        // 2. Logic nghiệp vụ (Giữ lại logic tạo mã của bạn)
        String newId = "LX-" + UUID.randomUUID().toString().substring(0, 8);
        loaiXeMoi.setMaLoai(newId);

        // 3. Lưu Entity
        LoaiXe loaiXeDaLuu = loaiXeRepository.save(loaiXeMoi);

        // 4. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(loaiXeDaLuu);
    }

    public LoaiXeDTO updateLoaiXe(String id, LoaiXeRequestDTO dto) {
        // 1. Tìm loại xe cũ
        LoaiXe loaiXeHienTai = timLoaiXeBangId(id);

        // 2. Cập nhật thông tin từ DTO
        loaiXeHienTai.setTenLoai(dto.getTenLoai());

        // 3. Lưu lại
        LoaiXe loaiXeDaCapNhat = loaiXeRepository.save(loaiXeHienTai);

        // 4. Chuyển Entity -> DTO
        return chuyenSangDTO(loaiXeDaCapNhat);
    }

    public void deleteLoaiXe(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        LoaiXe lx = timLoaiXeBangId(id);
        // 2. Nếu tìm thấy, thì xóa
        loaiXeRepository.delete(lx);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity LoaiXe sang LoaiXeDTO
     */
    private LoaiXeDTO chuyenSangDTO(LoaiXe entity) {
        if (entity == null)
            return null;

        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(entity.getMaLoai());
        dto.setTenLoai(entity.getTenLoai());
        // Không bao gồm danh sách xe hoặc bảng giá
        return dto;
    }

    /**
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private LoaiXe timLoaiXeBangId(String id) {
        return loaiXeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + id));
    }
    @Transactional(readOnly = true)
    public List<PhanBoLoaiXeDTO> getStatsPhanBoLoaiXe() {
        return loaiXeRepository.getPhanBoLoaiXe();
    }
}