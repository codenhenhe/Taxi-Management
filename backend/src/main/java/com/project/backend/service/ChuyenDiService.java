package com.project.backend.service;

import com.project.backend.dto.ChuyenDiDTO;
import com.project.backend.dto.ChuyenDiRequestDTO;
import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.ChuyenDi;
import com.project.backend.model.KhachHang;
import com.project.backend.model.Xe;
import com.project.backend.repository.ChuyenDiRepository;
import com.project.backend.repository.KhachHangRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public List<ChuyenDiDTO> getAllChuyenDi() {
        List<ChuyenDi> danhSachEntity = chuyenDiRepository.findAllWithDetails();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
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
        return chuyenSangDTO(chuyenDiDaCapNhat);
    }

    public List<ThongKeChuyenTheoGio> layThongKeChuyenTheoGio() {
        return chuyenDiRepository.thongKeChuyenTheoGio();
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Chuyển Entity -> DTO (làm phẳng)
     */
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

    /**
     * Tìm Entity theo ID (tùy chọn JOIN FETCH)
     */
    private ChuyenDi timChuyenDiBangId(String id, boolean useJoinFetch) {
        Optional<ChuyenDi> optionalCd = useJoinFetch
                ? chuyenDiRepository.findByIdWithDetails(id)
                : chuyenDiRepository.findById(id);

        return optionalCd.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + id));
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