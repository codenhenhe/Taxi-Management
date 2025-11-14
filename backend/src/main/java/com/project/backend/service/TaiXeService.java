package com.project.backend.service;

import com.project.backend.dto.RevenueByDriver;
import com.project.backend.dto.TaiXeDTO;
import com.project.backend.dto.TaiXeRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.TaiXe;
import com.project.backend.model.TrangThaiTaiXe;
import com.project.backend.repository.TaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaiXeService {

    @Autowired
    private TaiXeRepository taiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<TaiXeDTO> getAllTaiXe() {
        List<TaiXe> danhSachEntity = taiXeRepository.findAll();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    public TaiXeDTO getTaiXeById(String id) {
        TaiXe taiXeEntity = timTaiXeBangId(id);
        return chuyenSangDTO(taiXeEntity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public TaiXeDTO createTaiXe(TaiXeRequestDTO dto) {
        // Kiểm tra số điện thoại trùng (nếu cần)
        if (dto.getSoDienThoai() != null && taiXeRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
        }

        // 1. Chuyển DTO -> Entity
        TaiXe taiXeMoi = new TaiXe();
        taiXeMoi.setTenTaiXe(dto.getTenTaiXe());
        taiXeMoi.setSoHieuGPLX(dto.getSoHieuGPLX());
        taiXeMoi.setNgaySinh(dto.getNgaySinh());
        taiXeMoi.setSoDienThoai(dto.getSoDienThoai());

        // 2. Tạo ID duy nhất: TX-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaTaiXe();
        taiXeMoi.setMaTaiXe(newId);

        // Gán trạng thái mặc định
        taiXeMoi.setTrangThai(TrangThaiTaiXe.DANG_LAM_VIEC);

        // 3. Lưu Entity
        TaiXe taiXeDaLuu = taiXeRepository.save(taiXeMoi);

        // 4. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(taiXeDaLuu);
    }

    public TaiXeDTO updateTaiXe(String id, TaiXeRequestDTO dto) {
        TaiXe taiXeHienTai = timTaiXeBangId(id);

        // Kiểm tra số điện thoại trùng (ngoại trừ chính nó)
        if (dto.getSoDienThoai() != null
                && !dto.getSoDienThoai().equals(taiXeHienTai.getSoDienThoai())
                && taiXeRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng!");
        }

        taiXeHienTai.setTenTaiXe(dto.getTenTaiXe());
        taiXeHienTai.setSoDienThoai(dto.getSoDienThoai());
        taiXeHienTai.setSoHieuGPLX(dto.getSoHieuGPLX());
        taiXeHienTai.setNgaySinh(dto.getNgaySinh());
        taiXeHienTai.setTrangThai(dto.getTrangThai());

        TaiXe taiXeDaCapNhat = taiXeRepository.save(taiXeHienTai);
        return chuyenSangDTO(taiXeDaCapNhat);
    }

    public void deleteTaiXe(String id) {
        TaiXe tx = timTaiXeBangId(id);
        taiXeRepository.delete(tx);
    }

    // --- HÀM DOANH THU (GIỮ NGUYÊN) ---
    public List<RevenueByDriver> layDoanhThuTheoTaiXe(LocalDate date) {
        return taiXeRepository.getRevenueByDriver(date);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Chuyển Entity -> DTO
     */
    private TaiXeDTO chuyenSangDTO(TaiXe entity) {
        if (entity == null) return null;

        TaiXeDTO dto = new TaiXeDTO();
        dto.setMaTaiXe(entity.getMaTaiXe());
        dto.setTenTaiXe(entity.getTenTaiXe());
        dto.setSoHieuGPLX(entity.getSoHieuGPLX());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }

    /**
     * Tìm Entity theo ID, nếu không có thì ném lỗi
     */
    private TaiXe timTaiXeBangId(String id) {
        return taiXeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài xế với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã TX-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaTaiXe() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "TX" + code;

            if (!taiXeRepository.existsByMaTaiXe(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã tài xế duy nhất sau " + MAX_RETRIES + " lần thử.");
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