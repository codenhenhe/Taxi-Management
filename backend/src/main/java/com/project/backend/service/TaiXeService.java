package com.project.backend.service;

import com.project.backend.dto.RevenueByDriver;
import com.project.backend.dto.TaiXeDTO; // <-- Import
import com.project.backend.dto.TaiXeRequestDTO; // <-- Import
import com.project.backend.dto.TaiXeStatsDTO;
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.TaiXe;
import com.project.backend.model.TrangThaiTaiXe;
import com.project.backend.repository.TaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // <-- Import

@Service
public class TaiXeService {

    @Autowired
    private TaiXeRepository taiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<TaiXeDTO> getAllTaiXe() {
        List<TaiXe> danhSachEntity = taiXeRepository.findAll(); // <-- Lấy N tài xế (1 query)

        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // <-- Chuyển đổi
                .collect(Collectors.toList());
    }

    public TaiXeDTO getTaiXeById(String id) {
        // 1. Lấy Entity
        TaiXe taiXeEntity = timTaiXeBangId(id);
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(taiXeEntity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public TaiXeDTO createTaiXe(TaiXeRequestDTO dto) {
        // 1. Chuyển DTO -> Entity
        TaiXe taiXeMoi = new TaiXe();
        taiXeMoi.setTenTaiXe(dto.getTenTaiXe());
        taiXeMoi.setSoHieuGPLX(dto.getSoHieuGPLX());
        taiXeMoi.setNgaySinh(dto.getNgaySinh());
        taiXeMoi.setSoDienThoai(dto.getSoDienThoai());

        // 2. Logic nghiệp vụ (Giữ lại logic của bạn)
        String newId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        taiXeMoi.setMaTaiXe(newId);

        // Gán trạng thái mặc định
        taiXeMoi.setTrangThai(TrangThaiTaiXe.DANG_LAM_VIEC); // Hoặc TrangThaiTaiXe.RANH tùy bạn

        // 3. Lưu Entity
        TaiXe taiXeDaLuu = taiXeRepository.save(taiXeMoi);

        // 4. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(taiXeDaLuu);
    }

    public TaiXeDTO updateTaiXe(String id, TaiXeRequestDTO dto) {
        // 1. Tìm tài xế cũ
        TaiXe taiXeHienTai = timTaiXeBangId(id);

        // 2. Cập nhật thông tin từ DTO
        taiXeHienTai.setTenTaiXe(dto.getTenTaiXe());
        taiXeHienTai.setSoDienThoai(dto.getSoDienThoai());
        taiXeHienTai.setSoHieuGPLX(dto.getSoHieuGPLX());
        taiXeHienTai.setNgaySinh(dto.getNgaySinh());
        taiXeHienTai.setTrangThai(dto.getTrangThai());

        // 3. Lưu lại
        TaiXe taiXeDaCapNhat = taiXeRepository.save(taiXeHienTai);

        // 4. Chuyển Entity -> DTO
        return chuyenSangDTO(taiXeDaCapNhat);
    }

    public void deleteTaiXe(String id) {
        // 1. Tìm tài xế (để chắc chắn nó tồn tại)
        TaiXe tx = timTaiXeBangId(id);
        // 2. Nếu tìm thấy, thì xóa
        taiXeRepository.delete(tx);
    }

    // --- HÀM DOANH THU (GIỮ NGUYÊN) ---
    public List<RevenueByDriver> layDoanhThuTheoTaiXe(LocalDate date) {
        return taiXeRepository.getRevenueByDriver(date);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity TaiXe sang TaiXeDTO
     */
    private TaiXeDTO chuyenSangDTO(TaiXe entity) {
        if (entity == null)
            return null;

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
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private TaiXe timTaiXeBangId(String id) {
        return taiXeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài xế với ID: " + id));
    }

    @Transactional(readOnly = true) // Dùng @Transactional khi gọi native query
    public List<TaiXeStatsDTO> getTaiXeStats() {
        return taiXeRepository.getTaiXeStats();
    }
}