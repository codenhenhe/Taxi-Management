package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO; // <-- THÊM IMPORT
import com.project.backend.dto.XeDTO;
import com.project.backend.dto.XeRequestDTO;
import com.project.backend.dto.XeStatsDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.LoaiXe; // <-- Cần import này
import com.project.backend.model.TrangThaiXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.LoaiXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Service
public class XeService {

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<XeDTO> getAllXe() {
        // 1. Lấy List<Entity>
        List<Xe> danhSachEntity = xeRepository.findAll();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper bên dưới
                .collect(Collectors.toList());
    }

    public XeDTO getXeById(String id) {
        // 1. Lấy Entity
        Xe xeEntity = xeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(xeEntity);
    }

    // --- CÁC HÀM CUD (Nhận DTO, Trả về DTO) ---

    public XeDTO createXe(XeRequestDTO dto) {
        // 1. Tìm đối tượng liên quan (LoaiXe)
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // 2. Chuyển DTO -> Entity
        Xe xeMoi = new Xe();
        xeMoi.setBienSoXe(dto.getBienSoXe());
        xeMoi.setMauXe(dto.getMauXe());
        xeMoi.setNamSanXuat(dto.getNamSanXuat());
        xeMoi.setLoaiXe(loaiXe); // Gán object

        // 3. Logic nghiệp vụ (tự tạo mã, set default)
        String newId = "XE-" + UUID.randomUUID().toString().substring(0, 8);
        xeMoi.setMaXe(newId);
        xeMoi.setTrangThaiXe(TrangThaiXe.SAN_SANG); // Luôn set SAN_SANG khi mới tạo

        // 4. Lưu Entity
        Xe xeDaLuu = xeRepository.save(xeMoi);

        // 5. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(xeDaLuu);
    }

    public XeDTO updateXe(String id, XeRequestDTO dto) {
        // 1. Tìm Entity cũ
        Xe xeHienTai = xeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        // 2. Tìm LoaiXe mới (nếu có thay đổi)
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // 3. Cập nhật Entity từ DTO
        xeHienTai.setBienSoXe(dto.getBienSoXe());
        xeHienTai.setMauXe(dto.getMauXe());
        xeHienTai.setNamSanXuat(dto.getNamSanXuat());
        xeHienTai.setTrangThaiXe(dto.getTrangThaiXe()); // Cập nhật trạng thái
        xeHienTai.setLoaiXe(loaiXe); // Cập nhật loại xe

        // 4. Lưu Entity
        Xe xeDaCapNhat = xeRepository.save(xeHienTai);

        // 5. Chuyển Entity -> DTO
        return chuyenSangDTO(xeDaCapNhat);
    }

    public void deleteXe(String id) {
        Xe xe = xeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
        xeRepository.delete(xe);
    }

    private XeDTO chuyenSangDTO(Xe xeEntity) {
        if (xeEntity == null)
            return null;

        XeDTO dto = new XeDTO();
        dto.setMaXe(xeEntity.getMaXe());
        dto.setBienSoXe(xeEntity.getBienSoXe());
        dto.setMauXe(xeEntity.getMauXe());
        dto.setNamSanXuat(xeEntity.getNamSanXuat());
        dto.setTrangThaiXe(xeEntity.getTrangThaiXe());

        // SỬA LẠI CHỖ NÀY:
        // Load LAZY và chuyển đổi lồng sang LoaiXeDTO
        if (xeEntity.getLoaiXe() != null) {
            // Gọi hàm helper mới
            dto.setLoaiXe(chuyenLoaiXeSangDTO(xeEntity.getLoaiXe()));
        }

        return dto;
    }

    // --- THÊM HÀM HELPER MỚI CHO LOAIXE ---
    private LoaiXeDTO chuyenLoaiXeSangDTO(LoaiXe loaiXeEntity) {
        if (loaiXeEntity == null)
            return null;

        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(loaiXeEntity.getMaLoai());
        dto.setTenLoai(loaiXeEntity.getTenLoai());
        // Bỏ qua List<Xe>
        return dto;
    }
    // --- HÀM MỚI CHO THỐNG KÊ (E3) ---
    @Transactional(readOnly = true)
    public List<XeStatsDTO> getXeStats() {
        return xeRepository.getXeStats();
    }
}