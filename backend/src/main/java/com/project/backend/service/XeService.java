package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO;
import com.project.backend.dto.XeDTO;
import com.project.backend.dto.XeRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.LoaiXe;
import com.project.backend.dto.XeStatsDTO;
import com.project.backend.model.TrangThaiXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.LoaiXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; 
import jakarta.persistence.criteria.Join;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;


@Service
public class XeService {

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository; // Cần để lấy LoaiXe

    // --- CÁC HÀM GET (Trả về DTO) ---

    @Transactional(readOnly = true)
    public Page<XeDTO> getAllXe(String maXe, String bienSoXe, String mauXe, String namSanXuat, String trangThaiXe, String maLoai, Pageable pageable) {
        
        Specification<Xe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maXe != null && !maXe.isEmpty()) {
                predicates.add(cb.like(root.get("maXe"), "%" + maXe + "%"));
            }
            if (bienSoXe != null && !bienSoXe.isEmpty()) {
                predicates.add(cb.like(root.get("bienSoXe"), "%" + bienSoXe + "%"));
            }
            if (mauXe != null && !mauXe.isEmpty()) {
                predicates.add(cb.like(root.get("mauXe"), "%" + mauXe + "%"));
            }
            if (namSanXuat != null && !namSanXuat.isEmpty()) {
                predicates.add(cb.like(root.get("namSanXuat"), "%" + namSanXuat + "%"));
            }
            if (trangThaiXe != null && !trangThaiXe.isEmpty()) {
                try {
                    TrangThaiXe status = TrangThaiXe.valueOf(trangThaiXe);
                    predicates.add(cb.equal(root.get("trangThaiXe"), status));
                } catch (IllegalArgumentException e) {
                    // Bỏ qua
                }
            }
            
            // Lọc lồng nhau
            if (maLoai != null && !maLoai.isEmpty()) {
                Join<Xe, LoaiXe> loaiXeJoin = root.join("loaiXe");
                // Sửa: Dùng đúng tên trường 'maLoai' của Entity LoaiXe
                predicates.add(cb.equal(loaiXeJoin.get("maLoai"), maLoai));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Xe> pageOfEntities = xeRepository.findAll(spec, pageable);        
        return pageOfEntities.map(this::chuyenSangDTO);
    }

    @Transactional(readOnly = true)
    public XeDTO getXeById(String id) {
        Xe xeEntity = timXeBangId(id);
        return chuyenSangDTO(xeEntity);
    }

    // --- CÁC HÀM CUD (Sửa theo logic KhachHangService) ---

    @Transactional
    public XeDTO createXe(XeRequestDTO dto) { // <-- SỬA 1: Chỉ nhận DTO
        
        // Kiểm tra biển số trùng (nếu cần)
        if (dto.getBienSoXe() != null && xeRepository.existsByBienSoXe(dto.getBienSoXe())) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại!");
        }

        // SỬA 2: Tìm LoaiXe bằng 'dto.getMaLoai()'
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // 2. Chuyển DTO -> Entity
        Xe xeMoi = new Xe();
        xeMoi.setBienSoXe(dto.getBienSoXe());
        xeMoi.setMauXe(dto.getMauXe());
        xeMoi.setNamSanXuat(dto.getNamSanXuat());
        xeMoi.setLoaiXe(loaiXe); // Gán Entity LoaiXe đã tìm
        xeMoi.setTrangThaiXe(dto.getTrangThaiXe()); // Lấy trạng thái từ DTO

        // 3. Tạo ID duy nhất (Giống KhachHangService)
        String newId = generateUniqueMaXe();
        xeMoi.setMaXe(newId);

        // 4. Set trạng thái mặc định (Ghi đè nếu DTO không có)
        if (dto.getTrangThaiXe() == null) {
            xeMoi.setTrangThaiXe(TrangThaiXe.SAN_SANG);
        }

        // 5. Lưu Entity
        Xe xeDaLuu = xeRepository.save(xeMoi);

        // 6. Trả về DTO
        return chuyenSangDTO(xeDaLuu);
    }

    @Transactional
    public XeDTO updateXe(String id, XeRequestDTO dto) { // <-- SỬA 3: Nhận 'id' và 'dto'

        Xe xeHienTai = timXeBangId(id); // Tìm bằng 'id'

        // Kiểm tra biển số trùng (ngoại trừ chính nó)
        if (dto.getBienSoXe() != null
                && !dto.getBienSoXe().equals(xeHienTai.getBienSoXe())
                && xeRepository.existsByBienSoXe(dto.getBienSoXe())) {
            throw new IllegalArgumentException("Biển số xe đã được sử dụng!");
        }

        // SỬA 4: Tìm LoaiXe mới bằng 'dto.getMaLoai()'
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // Cập nhật thông tin
        xeHienTai.setBienSoXe(dto.getBienSoXe());
        xeHienTai.setMauXe(dto.getMauXe());
        xeHienTai.setNamSanXuat(dto.getNamSanXuat());
        xeHienTai.setTrangThaiXe(dto.getTrangThaiXe());
        xeHienTai.setLoaiXe(loaiXe); // Cập nhật LoaiXe

        Xe xeDaCapNhat = xeRepository.save(xeHienTai);

        return chuyenSangDTO(xeDaCapNhat);
    }

    @Transactional
    public void deleteXe(String id) {
        Xe xe = timXeBangId(id);
        xeRepository.delete(xe);
    }

    // --- HÀM HELPER (Chuyển đổi DTO) ---

    private XeDTO chuyenSangDTO(Xe entity) {
        if (entity == null) return null;

        XeDTO dto = new XeDTO();
        dto.setMaXe(entity.getMaXe());
        dto.setBienSoXe(entity.getBienSoXe());
        dto.setMauXe(entity.getMauXe());
        dto.setNamSanXuat(entity.getNamSanXuat());
        dto.setTrangThaiXe(entity.getTrangThaiXe());
        
        // Chuyển đổi object LoaiXe (Entity) lồng nhau sang LoaiXeDTO (DTO)
        if (entity.getLoaiXe() != null) {
            LoaiXeDTO loaiXeDTO = new LoaiXeDTO();
            loaiXeDTO.setMaLoai(entity.getLoaiXe().getMaLoai());
            loaiXeDTO.setTenLoai(entity.getLoaiXe().getTenLoai());
            loaiXeDTO.setSoGhe(entity.getLoaiXe().getSoGhe()); 
            dto.setLoaiXe(loaiXeDTO);
        }

        return dto;
    }

    private Xe timXeBangId(String id) {
        return xeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (Giống KhachHangService) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    private String generateUniqueMaXe() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "XE" + code;

            if (!xeRepository.existsById(fullId)) { // Dùng existsById (chuẩn)
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã xe duy nhất sau " + MAX_RETRIES + " lần thử.");
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    
    // --- HÀM MỚI CHO THỐNG KÊ (E3) ---
    @Transactional(readOnly = true)
    public List<XeStatsDTO> getXeStats() {
        return xeRepository.getXeStats();
    }
}