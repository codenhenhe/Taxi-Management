package com.project.backend.service;

import com.project.backend.dto.ChuyenDiDTO; // <-- Import
import com.project.backend.dto.ChuyenDiRequestDTO; // <-- Import
import com.project.backend.dto.ChuyenDiTheoNgayDTO;
import com.project.backend.dto.DoanhThuTheoNgayDTO;
import com.project.backend.dto.TopTaiXeDTO;
import com.project.backend.dto.SoSanhHomQuaDTO;
import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.dto.TongKetNgayDTO;
import com.project.backend.dto.TongKetThangDTO;
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.ChuyenDi;
import com.project.backend.model.KhachHang;
import com.project.backend.model.Xe;
import com.project.backend.repository.ChuyenDiRepository;
import com.project.backend.repository.KhachHangRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; 
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Service
public class ChuyenDiService {

    @Autowired
    private ChuyenDiRepository chuyenDiRepository;

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---
    @Transactional(readOnly = true)
    public Page<ChuyenDiDTO> getAllChuyenDi(String maChuyen, String diemDon, String diemTra, String tuNgayDon, String denNgayDon, String tuNgayTra, String denNgayTra, Double soKmDi, Double cuocPhi, String maXe, String maKhachHang, Pageable pageable) {
        
        LocalDateTime tuThoiGianDon = 
            (tuNgayDon != null && !tuNgayDon.isEmpty()) ? LocalDate.parse(tuNgayDon).atStartOfDay() : null;

        LocalDateTime denThoiGianDon = 
            (denNgayDon != null && !denNgayDon.isEmpty()) ? LocalDate.parse(denNgayDon).atTime(LocalTime.MAX) : null;

        LocalDateTime tuThoiGianTra = 
            (tuNgayTra != null && !tuNgayTra.isEmpty()) ? LocalDate.parse(tuNgayTra).atStartOfDay() : null;

        LocalDateTime denThoiGianTra = 
            (denNgayTra != null && !denNgayTra.isEmpty()) ? LocalDate.parse(denNgayTra).atTime(LocalTime.MAX) : null;


        Specification<ChuyenDi> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maChuyen != null && !maChuyen.isEmpty()) {
                predicates.add(cb.like(root.get("maChuyen"), "%" + maChuyen + "%"));
            }
            if (diemDon != null && !diemDon.isEmpty()) {
                predicates.add(cb.like(root.get("diemDon"), "%" + diemDon + "%"));
            }
            if (diemTra != null && !diemTra.isEmpty()) {
                predicates.add(cb.like(root.get("diemTra"), "%" + diemTra + "%"));
            }
            if (tuThoiGianDon != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tgDon"), tuThoiGianDon));
            }

            if (denThoiGianDon != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tgDon"), denThoiGianDon));
            }

            if (tuThoiGianTra != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tgTra"), tuThoiGianTra));
            }

            if (denThoiGianTra != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tgTra"), denThoiGianTra));
            }

            if (soKmDi != null) {
                predicates.add(cb.equal(root.get("soKmDi"), soKmDi));
            }
            if (cuocPhi != null) {
                predicates.add(cb.equal(root.get("cuocPhi"), cuocPhi));
            }

            if (maXe != null && !maXe.isEmpty()) {
                Join<ChuyenDi, Xe> xeJoin = root.join("xe"); // Tạo JOIN nếu chưa có
                // Lọc trên bảng Xe (đã join)
                predicates.add(cb.like(xeJoin.get("maXe"), "%" + maXe + "%"));
            }

            if (maKhachHang != null && !maKhachHang.isEmpty()) {
                Join<ChuyenDi, KhachHang> khachHangJoin = root.join("khachHang"); // Tạo JOIN nếu chưa có
                predicates.add(cb.like(khachHangJoin.get("maKhachHang"), "%" + maKhachHang + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<ChuyenDi> pageOfEntities = chuyenDiRepository.findAll(spec, pageable);        
        return pageOfEntities.map(this::chuyenSangDTO);
    }

    public ChuyenDiDTO getChuyenDiById(String id) {
        ChuyenDi entity = timChuyenDiBangId(id, true);

        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    @Transactional

    public ChuyenDiDTO createChuyenDi(ChuyenDiRequestDTO dto) {
        // 1. Tìm Xe và Khách Hàng
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + dto.getMaXe()));
        KhachHang kh = khachHangRepository.findById(dto.getMaKhachHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng: " + dto.getMaKhachHang()));

        // 2. Chuyển DTO -> Entity
        ChuyenDi chuyenDiMoi = new ChuyenDi();
        chuyenDiMoi.setDiemDon(dto.getDiemDon());
        chuyenDiMoi.setDiemTra(dto.getDiemTra());
        chuyenDiMoi.setXe(xe);
        chuyenDiMoi.setKhachHang(kh);

        // 3. Tạo ID duy nhất: CD-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaChuyen();
        chuyenDiMoi.setMaChuyen(newId);

        // 4. Gán thời gian đón
        chuyenDiMoi.setTgDon(LocalDateTime.now());

        // 5. Lưu Entity
        ChuyenDi chuyenDiDaLuu = chuyenDiRepository.save(chuyenDiMoi);

        // 6. Trả về DTO

        return chuyenSangDTO(chuyenDiDaLuu);
    }

    @Transactional
    public ChuyenDiDTO updateChuyenDi(String id, ChuyenDiRequestDTO dto) {
        ChuyenDi chuyenDiHienTai = timChuyenDiBangId(id, false);


        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + dto.getMaXe()));
        KhachHang kh = khachHangRepository.findById(dto.getMaKhachHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng: " + dto.getMaKhachHang()));

        chuyenDiHienTai.setDiemDon(dto.getDiemDon());
        chuyenDiHienTai.setDiemTra(dto.getDiemTra());
        chuyenDiHienTai.setXe(xe);
        chuyenDiHienTai.setKhachHang(kh);

        ChuyenDi chuyenDiDaCapNhat = chuyenDiRepository.save(chuyenDiHienTai);

        return chuyenSangDTO(chuyenDiDaCapNhat);
    }

    public void deleteChuyenDi(String id) {
        ChuyenDi cd = timChuyenDiBangId(id, false);

        chuyenDiRepository.delete(cd);
    }

    // --- CÁC HÀM NGHIỆP VỤ ---

    @Transactional
    public ChuyenDiDTO hoanTatChuyenDi(String id, Double soKmDi) {
        chuyenDiRepository.hoanTatChuyenDi(id, soKmDi);
        ChuyenDi chuyenDiDaCapNhat = timChuyenDiBangId(id, true);
        if (soKmDi != null && soKmDi < 0) {
            throw new IllegalArgumentException("Số km phải >= 0");
        }

        return chuyenSangDTO(chuyenDiDaCapNhat);
    }

    public List<ThongKeChuyenTheoGio> layThongKeChuyenTheoGio() {
        return chuyenDiRepository.thongKeChuyenTheoGio();
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---


    private ChuyenDiDTO chuyenSangDTO(ChuyenDi entity) {
        if (entity == null) return null;

        ChuyenDiDTO dto = new ChuyenDiDTO();

        dto.setMaChuyen(entity.getMaChuyen());
        dto.setDiemDon(entity.getDiemDon());
        dto.setDiemTra(entity.getDiemTra());
        dto.setTgDon(entity.getTgDon());
        dto.setTgTra(entity.getTgTra());
        dto.setSoKmDi(entity.getSoKmDi());
        dto.setCuocPhi(entity.getCuocPhi());

        if (entity.getXe() != null) {
            dto.setMaXe(entity.getXe().getMaXe());
            dto.setBienSoXe(entity.getXe().getBienSoXe());
        }

        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getTenKhachHang());
            dto.setSdtKhachHang(entity.getKhachHang().getSdt());

        }

        return dto;
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã CD-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaChuyen() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "CD" + code;

            if (!chuyenDiRepository.existsByMaChuyen(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã chuyến đi duy nhất sau " + MAX_RETRIES + " lần thử.");
    }


    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }    
    private ChuyenDi timChuyenDiBangId(String id, boolean useJoinFetch) {
        Optional<ChuyenDi> optionalCd;
        if (useJoinFetch) {
            optionalCd = chuyenDiRepository.findByIdWithDetails(id);
        } else {
            optionalCd = chuyenDiRepository.findById(id);
        }

        return optionalCd.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + id));
    }
    // E1: sp_so_voi_hom_qua
    @Transactional(readOnly = true)
    public List<SoSanhHomQuaDTO> getSoSanhHomQua() {
        return chuyenDiRepository.getSoSanhHomQua();
    }

    // E4: sp_chuyen_di_gan_day (Viết lại bằng JPA, trả về DTO)
    @Transactional(readOnly = true)
    public List<ChuyenDiDTO> getChuyenDiGanDay(int soChuyen) {
        // Lấy 0, soChuyen (ví dụ: trang 0, 10 phần tử)
        Pageable pageable = PageRequest.of(0, soChuyen); 
        
        List<ChuyenDi> entities = chuyenDiRepository.findAllByOrderByTgDonDesc(pageable);
        
        // Chuyển List<Entity> -> List<DTO> để tránh đệ quy
        return entities.stream()
                .map(this::chuyenSangDTO) // Dùng helper
                .collect(Collectors.toList());
    }

    // E5: sp_tinh_doanh_thu_hom_nay
    @Transactional(readOnly = true)
    public BigDecimal getDoanhThuHomNay() {
        return chuyenDiRepository.getDoanhThuHomNay();
    }

    // E6: sp_tinh_doanh_thu_chuyen_di_theo_so_ngay
    @Transactional(readOnly = true)
    public List<TongKetNgayDTO> getTongKetTheoSoNgay(int soNgay, LocalDate ngayTinh) {
        return chuyenDiRepository.getTongKetTheoSoNgay(soNgay, ngayTinh);
    }

    // E7: sp_tinh_doanh_thu_chuyen_di_theo_thang
    @Transactional(readOnly = true)
    public List<TongKetThangDTO> getTongKetTheoThang(int thangBD, int thangKT, int nam) {
        return chuyenDiRepository.getTongKetTheoThang(thangBD, thangKT, nam);
    }
    
    @Transactional(readOnly = true)
    public List<DoanhThuTheoNgayDTO> getStatsRevenue(int soNgay) {
        return chuyenDiRepository.getDoanhThuTheoNgay(soNgay);
    }

    @Transactional(readOnly = true)
    public List<ChuyenDiTheoNgayDTO> getStatsTrips(int soNgay) {
        return chuyenDiRepository.getChuyenDiTheoNgay(soNgay);
    }

    @Transactional(readOnly = true)
    public List<TopTaiXeDTO> getStatsTopTaiXe() {
        return chuyenDiRepository.getTopTaiXe();
    }
}