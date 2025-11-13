package com.project.backend.service;

import com.project.backend.dto.KhachHangDTO; // <-- Import
import com.project.backend.dto.KhachHangRequestDTO; // <-- Import
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng exception này)
import com.project.backend.model.KhachHang;
import com.project.backend.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID; // <-- Dùng để tạo ID
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<KhachHangDTO> getAllKhachHang() {
        // 1. Lấy List<Entity>
        List<KhachHang> danhSachEntity = khachHangRepository.findAll();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper bên dưới
                .collect(Collectors.toList());
    }

    public KhachHangDTO getKhachHangById(String id) {
        // 1. Lấy Entity
        KhachHang khachHangEntity = timKhachHangBangId(id);
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(khachHangEntity);
    }

    // --- CÁC HÀM CUD (Nhận DTO, Trả về DTO) ---

    public KhachHangDTO createKhachHang(KhachHangRequestDTO dto) {
        // (Đây là nơi bạn có thể thêm logic kiểm tra
        // ví dụ: kiểm tra SĐT đã tồn tại chưa)

        // 1. Chuyển DTO -> Entity
        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.setTenKhachHang(dto.getTenKhachHang());
        khachHangMoi.setSdt(dto.getSdt());

        // 2. Thêm logic nghiệp vụ (ví dụ: Tự tạo ID)
        String newId = "KH-" + UUID.randomUUID().toString().substring(0, 8);
        khachHangMoi.setMaKhachHang(newId);

        // 3. Lưu Entity
        KhachHang khachHangDaLuu = khachHangRepository.save(khachHangMoi);

        // 4. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(khachHangDaLuu);
    }

    public KhachHangDTO updateKhachHang(String id, KhachHangRequestDTO dto) {
        // 1. Tìm khách hàng cũ
        KhachHang khachHangHienTai = timKhachHangBangId(id);

        // 2. Cập nhật thông tin từ DTO
        khachHangHienTai.setTenKhachHang(dto.getTenKhachHang());
        khachHangHienTai.setSdt(dto.getSdt());

        // 3. Lưu lại
        KhachHang khachHangDaCapNhat = khachHangRepository.save(khachHangHienTai);

        // 4. Chuyển Entity -> DTO
        return chuyenSangDTO(khachHangDaCapNhat);
    }

    public void deleteKhachHang(String id) {
        // 1. Tìm khách hàng (để chắc chắn nó tồn tại)
        KhachHang kh = timKhachHangBangId(id);
        // 2. Nếu tìm thấy, thì xóa
        khachHangRepository.delete(kh);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity KhachHang sang KhachHangDTO
     */
    private KhachHangDTO chuyenSangDTO(KhachHang entity) {
        if (entity == null)
            return null;

        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(entity.getMaKhachHang());
        dto.setTenKhachHang(entity.getTenKhachHang());
        dto.setSdt(entity.getSdt());
        return dto;
    }

    /**
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private KhachHang timKhachHangBangId(String id) {
        // Ném lỗi ResourceNotFoundException (Giả sử bạn có class này)
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
    }
}