package com.project.backend.service;

import com.project.backend.dto.BangGiaDTO; // <-- Import
import com.project.backend.dto.BangGiaRequestDTO; // <-- Import
import com.project.backend.dto.LoaiXeDTO; // <-- Import
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.BangGia;
import com.project.backend.model.LoaiXe;
import com.project.backend.repository.BangGiaRepository;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors; // <-- Import

@Service
public class BangGiaService {

    @Autowired
    private BangGiaRepository bangGiaRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<BangGiaDTO> getAllBangGia() {
        // 1. Dùng hàm JOIN FETCH để tránh N+1 Query
        List<BangGia> danhSachEntity = bangGiaRepository.findAllWithLoaiXe();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper
                .collect(Collectors.toList());
    }

    public BangGiaDTO getBangGiaById(String id) {
        // 1. Dùng hàm JOIN FETCH
        BangGia entity = timBangGiaBangId(id, true); // true = dùng JOIN FETCH
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public BangGiaDTO createBangGia(BangGiaRequestDTO dto) {
        // 1. Tìm loại xe tương ứng
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + dto.getMaLoai()));

        // 2. Chuyển DTO -> Entity
        BangGia bangGiaMoi = new BangGia();
        bangGiaMoi.setGiaKhoiDiem(dto.getGiaKhoiDiem());
        bangGiaMoi.setGiaTheoKm(dto.getGiaTheoKm());
        bangGiaMoi.setPhuThu(dto.getPhuThu());
        bangGiaMoi.setLoaiXe(loaiXe); // Gán object

        // 3. Logic nghiệp vụ (Tự tạo mã ID)
        String newId = "BG-" + UUID.randomUUID().toString().substring(0, 8);
        bangGiaMoi.setMaBangGia(newId);

        // 4. Lưu vào CSDL
        BangGia bangGiaDaLuu = bangGiaRepository.save(bangGiaMoi);

        // 5. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(bangGiaDaLuu);
    }

    public BangGiaDTO updateBangGia(String id, BangGiaRequestDTO dto) {
        // 1. Tìm bảng giá cũ
        BangGia bangGiaHienTai = timBangGiaBangId(id, false); // false = không cần JOIN FETCH

        // 2. Tìm LoaiXe mới (nếu có thay đổi)
        LoaiXe loaiXeMoi = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + dto.getMaLoai()));

        // 3. Cập nhật thông tin từ DTO
        bangGiaHienTai.setGiaKhoiDiem(dto.getGiaKhoiDiem());
        bangGiaHienTai.setGiaTheoKm(dto.getGiaTheoKm());
        bangGiaHienTai.setPhuThu(dto.getPhuThu());
        bangGiaHienTai.setLoaiXe(loaiXeMoi); // Cập nhật cả loại xe

        // 4. Lưu lại
        BangGia bangGiaDaCapNhat = bangGiaRepository.save(bangGiaHienTai);

        // 5. Chuyển Entity -> DTO
        return chuyenSangDTO(bangGiaDaCapNhat);
    }

    public void deleteBangGia(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        BangGia bg = timBangGiaBangId(id, false);
        // 2. Nếu tìm thấy, thì xóa
        bangGiaRepository.delete(bg);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity BangGia sang BangGiaDTO
     */
    private BangGiaDTO chuyenSangDTO(BangGia entity) {
        if (entity == null)
            return null;

        BangGiaDTO dto = new BangGiaDTO();
        dto.setMaBangGia(entity.getMaBangGia());
        dto.setGiaKhoiDiem(entity.getGiaKhoiDiem());
        dto.setGiaTheoKm(entity.getGiaTheoKm());
        dto.setPhuThu(entity.getPhuThu());

        // Chuyển lồng LoaiXe sang LoaiXeDTO
        if (entity.getLoaiXe() != null) {
            dto.setLoaiXe(chuyenLoaiXeSangDTO(entity.getLoaiXe()));
        }

        return dto;
    }

    /**
     * Hàm private để chuyển Entity LoaiXe sang LoaiXeDTO
     * (Copy từ LoaiXeService)
     */
    private LoaiXeDTO chuyenLoaiXeSangDTO(LoaiXe loaiXeEntity) {
        if (loaiXeEntity == null)
            return null;

        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(loaiXeEntity.getMaLoai());
        dto.setTenLoai(loaiXeEntity.getTenLoai());
        return dto;
    }

    /**
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private BangGia timBangGiaBangId(String id, boolean useJoinFetch) {
        Optional<BangGia> optionalBg;
        if (useJoinFetch) {
            optionalBg = bangGiaRepository.findByIdWithLoaiXe(id);
        } else {
            optionalBg = bangGiaRepository.findById(id);
        }

        return optionalBg.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bảng giá với ID: " + id));
    }
}