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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@Service
public class PhanCongXeService {

    @Autowired
    private PhanCongXeRepository phanCongXeRepository;
    @Autowired
    private XeRepository xeRepository;
    @Autowired
    private TaiXeRepository taiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<PhanCongXeDTO> getAllPhanCongXe() {
        List<PhanCongXe> danhSachEntity = phanCongXeRepository.findAllWithDetails();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)
                .collect(Collectors.toList());
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