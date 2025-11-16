package com.project.backend.service;

import com.project.backend.dto.KetThucPhanCongRequestDTO;
import com.project.backend.dto.PhanCongXeDTO;
import com.project.backend.dto.PhanCongXeRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.PhanCongXe;
import com.project.backend.model.PhanCongXeId;
import com.project.backend.model.TaiXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.PhanCongXeRepository;
import com.project.backend.repository.TaiXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate; 
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.format.DateTimeParseException; 

@Service
public class PhanCongXeService {

    @Autowired
    private PhanCongXeRepository phanCongXeRepository;
    @Autowired
    private XeRepository xeRepository;
    @Autowired
    private TaiXeRepository taiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---
    @Transactional(readOnly = true)
    public Page<PhanCongXeDTO> getAllPhanCongXe(
            String maTaiXe, String maXe, 
            String tuTGBatDau, String denTGBatDau, 
            String tuTGKetThuc, String denTGKetThuc, 
            Pageable pageable) {
        
        // --- 1. SỬA LỖI: Dùng y hệt style của ChuyenDiService ---
        // Gán biến 1 lần duy nhất để nó là "effectively final"
        // (Lưu ý: Cách này sẽ ném lỗi 500 nếu frontend gửi sai định dạng ngày)
        LocalDateTime tuThoiGianBatDau = 
            (tuTGBatDau != null && !tuTGBatDau.isEmpty()) ? LocalDate.parse(tuTGBatDau).atStartOfDay() : null;

        LocalDateTime denThoiGianBatDau = 
            (denTGBatDau != null && !denTGBatDau.isEmpty()) ? LocalDate.parse(denTGBatDau).atTime(LocalTime.MAX) : null;

        LocalDateTime tuThoiGianKetThuc = 
            (tuTGKetThuc != null && !tuTGKetThuc.isEmpty()) ? LocalDate.parse(tuTGKetThuc).atStartOfDay() : null;

        LocalDateTime denThoiGianKetThuc = 
            (denTGKetThuc != null && !denTGKetThuc.isEmpty()) ? LocalDate.parse(denTGKetThuc).atTime(LocalTime.MAX) : null;
        // --- KẾT THÚC SỬA ---

        Specification<PhanCongXe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- 2. SỬA LỖI: Lọc trên 'id' ---
            if (maXe != null && !maXe.isEmpty()) {
                predicates.add(cb.like(root.get("id").get("maXe"), "%" + maXe + "%"));
            }

            if (maTaiXe != null && !maTaiXe.isEmpty()) {
                predicates.add(cb.like(root.get("id").get("maTaiXe"), "%" + maTaiXe + "%"));
            }

            if (tuThoiGianBatDau != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("id").get("thoiGianBatDau"), tuThoiGianBatDau));
            }

            if (denThoiGianBatDau != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("id").get("thoiGianBatDau"), denThoiGianBatDau));
            }
            // --- KẾT THÚC SỬA ---

            // Lọc trên trường thường (Đã đúng)
            if (tuThoiGianKetThuc != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("thoiGianKetThuc"), tuThoiGianKetThuc));
            }

            if (denThoiGianKetThuc != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("thoiGianKetThuc"), denThoiGianKetThuc));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<PhanCongXe> pageOfEntities = phanCongXeRepository.findAll(spec, pageable);        
        return pageOfEntities.map(this::chuyenSangDTO);
    }

    public PhanCongXeDTO getPhanCongXeById(PhanCongXeId id) {
        PhanCongXe entity = timPhanCongBangId(id, true);
        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM NGHIỆP VỤ (Nhận RequestDTO, Trả về DTO) ---

    @Transactional
    public PhanCongXeDTO createPhanCongXe(PhanCongXeRequestDTO dto) {
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + dto.getMaXe()));
        TaiXe taiXe = taiXeRepository.findById(dto.getMaTaiXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài xế: " + dto.getMaTaiXe()));

        LocalDateTime thoiGianBatDauThucTe = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        PhanCongXeId newId = new PhanCongXeId(dto.getMaTaiXe(), dto.getMaXe(), thoiGianBatDauThucTe);

        if (phanCongXeRepository.existsById(newId)) {
            throw new RuntimeException("Ca phân công này đã tồn tại.");
        }

        PhanCongXe phanCongMoi = new PhanCongXe();
        phanCongMoi.setId(newId);
        phanCongMoi.setXe(xe);
        phanCongMoi.setTaiXe(taiXe);

        PhanCongXe phanCongDaLuu = phanCongXeRepository.save(phanCongMoi);
        return chuyenSangDTO(phanCongDaLuu);
    }

    /**
     * HÀM ĐÃ SỬA THEO LOGIC MỚI
     * (Xóa bỏ hàm cũ dùng findActiveAssignmentByTaiXe)
     */
    @Transactional
    public PhanCongXeDTO ketThucPhanCong(KetThucPhanCongRequestDTO dto) {

        // 1. Tạo khóa chính phức hợp từ DTO
        PhanCongXeId id = new PhanCongXeId(
                dto.getMaTaiXe(),
                dto.getMaXe(),
                dto.getThoiGianBatDau());

        // 2. Tìm ca phân công chính xác bằng hàm helper (dùng JOIN FETCH)
        PhanCongXe phanCongHienTai = timPhanCongBangId(id, true);

        // 3. (Tùy chọn) Kiểm tra xem ca này đã kết thúc chưa
        if (phanCongHienTai.getThoiGianKetThuc() != null) {
            throw new RuntimeException("Ca phân công này đã được kết thúc từ trước.");
        }

        // 4. Cập nhật thời gian kết thúc
        phanCongHienTai.setThoiGianKetThuc(LocalDateTime.now());

        // 5. Lưu
        PhanCongXe phanCongDaLuu = phanCongXeRepository.save(phanCongHienTai);

        // 6. Trả về DTO (đã có đủ thông tin Xe/Tài xế vì dùng JOIN FETCH)
        return chuyenSangDTO(phanCongDaLuu);
    }

    public void deletePhanCongXe(PhanCongXeId id) {
        if (!phanCongXeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công để xóa." + id);
        }
        phanCongXeRepository.deleteById(id);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    private PhanCongXeDTO chuyenSangDTO(PhanCongXe entity) {
        if (entity == null)
            return null;

        PhanCongXeDTO dto = new PhanCongXeDTO();
        dto.setMaTaiXe(entity.getId().getMaTaiXe());
        dto.setMaXe(entity.getId().getMaXe());
        dto.setThoiGianBatDau(entity.getId().getThoiGianBatDau());
        dto.setThoiGianKetThuc(entity.getThoiGianKetThuc());

        if (entity.getTaiXe() != null) {
            dto.setTenTaiXe(entity.getTaiXe().getTenTaiXe());
        }
        if (entity.getXe() != null) {
            dto.setBienSoXe(entity.getXe().getBienSoXe());
        }

        return dto;
    }

    /**
     * HÀM HELPER BẠN ĐÃ DÁN BỊ THIẾU
     * (Hàm này phải nằm trong class)
     */
    private PhanCongXe timPhanCongBangId(PhanCongXeId id, boolean useJoinFetch) {
        Optional<PhanCongXe> optionalPcx;
        if (useJoinFetch) {
            optionalPcx = phanCongXeRepository.findByIdWithDetails(id);
        } else {
            optionalPcx = phanCongXeRepository.findById(id);
        }

        return optionalPcx.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id));
    }
}