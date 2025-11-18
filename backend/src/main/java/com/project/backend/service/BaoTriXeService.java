package com.project.backend.service;

import com.project.backend.dto.BaoTriXeDTO;
import com.project.backend.dto.BaoTriXeRequestDTO;
import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.exception.ResourceNotFoundException;

import com.project.backend.model.BaoTriXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.BaoTriXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; 
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class BaoTriXeService {

    @Autowired
    private BaoTriXeRepository baoTriXeRepository;

    @Autowired
    private XeRepository xeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---
    @Transactional(readOnly = true)
    public Page<BaoTriXeDTO> getAllBaoTriXe(String maBaoTri, String loaiBaoTri, Double chiPhi, String maXe, String bienSoXe, Pageable pageable) {
        
        Specification<BaoTriXe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maBaoTri != null && !maBaoTri.isEmpty()) {
                predicates.add(cb.like(root.get("maBaoTri"), "%" + maBaoTri + "%"));
            }
            if (loaiBaoTri != null && !loaiBaoTri.isEmpty()) {
                predicates.add(cb.like(root.get("loaiBaoTri"), "%" + loaiBaoTri + "%"));
            }
            if (chiPhi != null) {
                predicates.add(cb.equal(root.get("chiPhi"), chiPhi));
            }

            Join<BaoTriXe, Xe> xeJoin = null;

            if (maXe != null && !maXe.isEmpty()) {
                if (xeJoin == null) {
                    xeJoin = root.join("xe"); // Tạo JOIN nếu chưa có
                }
                // Lọc trên bảng Xe (đã join)
                predicates.add(cb.like(xeJoin.get("maXe"), "%" + maXe + "%"));
            }
            
            if (bienSoXe != null && !bienSoXe.isEmpty()) {
                if (xeJoin == null) {
                    xeJoin = root.join("xe"); // Tạo JOIN nếu chưa có
                }
                // Lọc trên bảng Xe (đã join)
                predicates.add(cb.like(xeJoin.get("bienSoXe"), "%" + bienSoXe + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<BaoTriXe> pageOfEntities = baoTriXeRepository.findAll(spec, pageable);        
        return pageOfEntities.map(this::chuyenSangDTO);
    }

    public BaoTriXeDTO getBaoTriXeById(String id) {
        BaoTriXe entity = timBaoTriXeBangId(id, true);

        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    @Transactional
    public BaoTriXeDTO createBaoTriXe(BaoTriXeRequestDTO dto) {
        // 1. Tìm Xe

        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + dto.getMaXe()));

        // 2. Chuyển DTO -> Entity
        BaoTriXe baoTriXeMoi = new BaoTriXe();
        baoTriXeMoi.setNgayBaoTri(dto.getNgayBaoTri());
        baoTriXeMoi.setLoaiBaoTri(dto.getLoaiBaoTri());
        baoTriXeMoi.setChiPhi(dto.getChiPhi());
        baoTriXeMoi.setMoTa(dto.getMoTa());

        baoTriXeMoi.setXe(xe);
        if (baoTriXeMoi.getChiPhi() != null && baoTriXeMoi.getChiPhi() < 0) {
            throw new IllegalArgumentException("Chi phí phải >= 0");
        }

        // 3. Tạo ID duy nhất: BT-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaBaoTri();
        baoTriXeMoi.setMaBaoTri(newId);

        // 4. Lưu (Trigger sẽ chạy)
        BaoTriXe baoTriXeDaLuu = baoTriXeRepository.save(baoTriXeMoi);

        // 5. Trả về DTO

        return chuyenSangDTO(baoTriXeDaLuu);
    }

    @Transactional
    public BaoTriXeDTO updateBaoTriXe(String id, BaoTriXeRequestDTO dto) {
        BaoTriXe baoTriXeHienTai = timBaoTriXeBangId(id, false);

        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + dto.getMaXe()));


        baoTriXeHienTai.setNgayBaoTri(dto.getNgayBaoTri());
        baoTriXeHienTai.setLoaiBaoTri(dto.getLoaiBaoTri());
        baoTriXeHienTai.setChiPhi(dto.getChiPhi());
        baoTriXeHienTai.setMoTa(dto.getMoTa());

        baoTriXeHienTai.setXe(xe);
        if (baoTriXeHienTai.getChiPhi() != null && baoTriXeHienTai.getChiPhi() < 0) {
            throw new IllegalArgumentException("Chi phí phải >= 0");
        }


        BaoTriXe baoTriXeDaCapNhat = baoTriXeRepository.save(baoTriXeHienTai);

        return chuyenSangDTO(baoTriXeDaCapNhat);
    }

    public void deleteBaoTriXe(String id) {
        BaoTriXe bx = timBaoTriXeBangId(id, false);

        baoTriXeRepository.delete(bx);
    }

    // --- HÀM THỐNG KÊ (Giữ nguyên) ---
    public List<ThongKePhiBaoTriHangThang> layThongKeChiPhiBaoTri(int year) {
        return baoTriXeRepository.getMonthlyMaintenanceCost(year);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    private BaoTriXeDTO chuyenSangDTO(BaoTriXe entity) {
        if (entity == null) return null;

        BaoTriXeDTO dto = new BaoTriXeDTO();

        dto.setMaBaoTri(entity.getMaBaoTri());
        dto.setNgayBaoTri(entity.getNgayBaoTri());
        dto.setLoaiBaoTri(entity.getLoaiBaoTri());
        dto.setChiPhi(entity.getChiPhi());
        dto.setMoTa(entity.getMoTa());

        if (entity.getXe() != null) {
            dto.setMaXe(entity.getXe().getMaXe());
            dto.setBienSoXe(entity.getXe().getBienSoXe());
        }

        return dto;
    }


    private BaoTriXe timBaoTriXeBangId(String id, boolean useJoinFetch) {
        Optional<BaoTriXe> optionalBx = useJoinFetch
                ? baoTriXeRepository.findByIdWithXe(id)
                : baoTriXeRepository.findById(id);


        return optionalBx
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử bảo trì với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã BT-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaBaoTri() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "BT" + code;

            if (!baoTriXeRepository.existsByMaBaoTri(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã bảo trì duy nhất sau " + MAX_RETRIES + " lần thử.");
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