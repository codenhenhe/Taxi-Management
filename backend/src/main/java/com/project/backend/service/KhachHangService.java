package com.project.backend.service;

import com.project.backend.dto.KhachHangDTO;
import com.project.backend.dto.KhachHangRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.KhachHang;
import com.project.backend.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<KhachHangDTO> getAllKhachHang() {
        List<KhachHang> danhSachEntity = khachHangRepository.findAll();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    public KhachHangDTO getKhachHangById(String id) {
        KhachHang khachHangEntity = timKhachHangBangId(id);
        return chuyenSangDTO(khachHangEntity);
    }

    // --- CÁC HÀM CUD (Nhận DTO, Trả về DTO) ---

    public KhachHangDTO createKhachHang(KhachHangRequestDTO dto) {
        // Kiểm tra SĐT trùng (nếu cần)
        if (dto.getSdt() != null && khachHangRepository.existsBySdt(dto.getSdt())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
        }

        // 1. Chuyển DTO -> Entity
        KhachHang khachHangMoi = new KhachHang();
        khachHangMoi.setTenKhachHang(dto.getTenKhachHang());
        khachHangMoi.setSdt(dto.getSdt());

        // 2. Tạo ID duy nhất: KH-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaKhachHang();
        khachHangMoi.setMaKhachHang(newId);

        // 3. Lưu Entity
        KhachHang khachHangDaLuu = khachHangRepository.save(khachHangMoi);

        // 4. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(khachHangDaLuu);
    }

    public KhachHangDTO updateKhachHang(String id, KhachHangRequestDTO dto) {
        KhachHang khachHangHienTai = timKhachHangBangId(id);

        // Kiểm tra SĐT trùng (ngoại trừ chính nó)
        if (dto.getSdt() != null && !dto.getSdt().equals(khachHangHienTai.getSdt())
                && khachHangRepository.existsBySdt(dto.getSdt())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
        }

        khachHangHienTai.setTenKhachHang(dto.getTenKhachHang());
        khachHangHienTai.setSdt(dto.getSdt());

        KhachHang khachHangDaCapNhat = khachHangRepository.save(khachHangHienTai);
        return chuyenSangDTO(khachHangDaCapNhat);
    }

    public void deleteKhachHang(String id) {
        KhachHang kh = timKhachHangBangId(id);
        khachHangRepository.delete(kh);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Chuyển Entity -> DTO
     */
    private KhachHangDTO chuyenSangDTO(KhachHang entity) {
        if (entity == null) return null;

        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(entity.getMaKhachHang());
        dto.setTenKhachHang(entity.getTenKhachHang());
        dto.setSdt(entity.getSdt());
        return dto;
    }

    /**
     * Tìm Entity theo ID, nếu không có thì ném lỗi
     */
    private KhachHang timKhachHangBangId(String id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã KH-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaKhachHang() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "KH" + code;

            if (!khachHangRepository.existsByMaKhachHang(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã khách hàng duy nhất sau " + MAX_RETRIES + " lần thử.");
    }

    /**
     * Sinh 8 ký tự ngẫu nhiên từ A-Z, 0-9
     */
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}