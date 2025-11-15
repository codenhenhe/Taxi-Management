package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO;
import com.project.backend.dto.LoaiXeRequestDTO;
import com.project.backend.dto.PhanBoLoaiXeDTO;
import com.project.backend.exception.ResourceNotFoundException;

import com.project.backend.model.LoaiXe;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page; // <-- 1. Import
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; 

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;


@Service
public class LoaiXeService {

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---
    public Page<LoaiXeDTO> getAllLoaiXe(String maLoai, String tenLoai, String soGhe, Pageable pageable) {
        
        // 1. Tạo Specification (bộ lọc động)
        Specification<LoaiXe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maLoai != null && !maLoai.isEmpty()) {
                predicates.add(cb.like(root.get("maLoai"), "%" + maLoai + "%"));
            }
            if (tenLoai != null && !tenLoai.isEmpty()) {
                predicates.add(cb.like(root.get("tenLoai"), "%" + tenLoai + "%"));
            }
            if (soGhe != null && !soGhe.isEmpty()) {
                predicates.add(cb.equal(root.get("soGhe"), soGhe));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 2. Gọi repository với cả bộ lọc VÀ sắp xếp
        Page<LoaiXe> pageOfEntities = loaiXeRepository.findAll(spec, pageable);        
        // 3. Chuyển sang DTO
        return pageOfEntities.map(this::chuyenSangDTO);
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


    private LoaiXeDTO chuyenSangDTO(LoaiXe entity) {
        if (entity == null) return null;


        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(entity.getMaLoai());
        dto.setTenLoai(entity.getTenLoai());
        dto.setSoGhe(entity.getSoGhe());

        return dto;
    }


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
    @Transactional(readOnly = true)
    public List<PhanBoLoaiXeDTO> getStatsPhanBoLoaiXe() {
        return loaiXeRepository.getPhanBoLoaiXe();
    }
}