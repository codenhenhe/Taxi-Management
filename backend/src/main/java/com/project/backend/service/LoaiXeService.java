package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO;
import com.project.backend.dto.LoaiXeRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.LoaiXe;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoaiXeService {

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<LoaiXeDTO> getAllLoaiXe() {
        List<LoaiXe> danhSachEntity = loaiXeRepository.findAll();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
    }

    public LoaiXeDTO getLoaiXeById(String id) {
        LoaiXe loaiXeEntity = timLoaiXeBangId(id);
        return chuyenSangDTO(loaiXeEntity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public LoaiXeDTO createLoaiXe(LoaiXeRequestDTO dto) {
        // Kiểm tra tên loại xe trùng (nếu cần)
        if (dto.getTenLoai() != null && loaiXeRepository.existsByTenLoai(dto.getTenLoai())) {
            throw new IllegalArgumentException("Tên loại xe đã tồn tại!");
        }

        // 1. Chuyển DTO -> Entity
        LoaiXe loaiXeMoi = new LoaiXe();
        loaiXeMoi.setTenLoai(dto.getTenLoai());
        loaiXeMoi.setSoGhe(dto.getSoGhe());

        // 2. Tạo ID duy nhất: LX-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaLoai();
        loaiXeMoi.setMaLoai(newId);

        // 3. Lưu Entity
        LoaiXe loaiXeDaLuu = loaiXeRepository.save(loaiXeMoi);

        // 4. Trả về DTO
        return chuyenSangDTO(loaiXeDaLuu);
    }

    public LoaiXeDTO updateLoaiXe(String id, LoaiXeRequestDTO dto) {
        LoaiXe loaiXeHienTai = timLoaiXeBangId(id);

        // Kiểm tra tên loại xe trùng (ngoại trừ chính nó)
        if (dto.getTenLoai() != null
                && !dto.getTenLoai().equals(loaiXeHienTai.getTenLoai())
                && loaiXeRepository.existsByTenLoai(dto.getTenLoai())) {
            throw new IllegalArgumentException("Tên loại xe đã được sử dụng!");
        }

        loaiXeHienTai.setTenLoai(dto.getTenLoai());
        loaiXeHienTai.setSoGhe(dto.getSoGhe());

        LoaiXe loaiXeDaCapNhat = loaiXeRepository.save(loaiXeHienTai);
        return chuyenSangDTO(loaiXeDaCapNhat);
    }

    public void deleteLoaiXe(String id) {
        LoaiXe lx = timLoaiXeBangId(id);
        loaiXeRepository.delete(lx);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Chuyển Entity -> DTO
     */
    private LoaiXeDTO chuyenSangDTO(LoaiXe entity) {
        if (entity == null) return null;

        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(entity.getMaLoai());
        dto.setTenLoai(entity.getTenLoai());
        dto.setSoGhe(entity.getSoGhe());
        return dto;
    }

    /**
     * Tìm Entity theo ID, nếu không có thì ném lỗi
     */
    private LoaiXe timLoaiXeBangId(String id) {
        return loaiXeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã LX-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaLoai() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "LX" + code;

            if (!loaiXeRepository.existsByMaLoai(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã loại xe duy nhất sau " + MAX_RETRIES + " lần thử.");
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