package com.project.backend.service;

import com.project.backend.model.ChuyenDi;
import com.project.backend.model.KhachHang;
import com.project.backend.model.Xe;
import com.project.backend.repository.ChuyenDiRepository;
import com.project.backend.repository.KhachHangRepository;
import com.project.backend.repository.XeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChuyenDiService {

    @Autowired
    private ChuyenDiRepository chuyenDiRepository;

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    /**
     * Hàm 1: Lấy tất cả chuyến đi
     */
    public List<ChuyenDi> getAllChuyenDi() {
        return chuyenDiRepository.findAll();
    }

    /**
     * Hàm 2: Lấy chuyến đi theo ID
     */
    public ChuyenDi getChuyenDiById(String id) {
        Optional<ChuyenDi> cd = chuyenDiRepository.findById(id);
        return cd.orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một chuyến đi mới
     * (Logic này sẽ kích hoạt 2 trigger trong schema.sql) [cite:
     * uploaded:schema.sql]
     */
    public ChuyenDi createChuyenDi(ChuyenDi chuyenDi, String maXe, String maKhachHang) {

        // 1. Tìm Xe và Khách Hàng
        Xe xe = xeRepository.findById(maXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe: " + maXe));
        KhachHang kh = khachHangRepository.findById(maKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + maKhachHang));

        // 2. Tự động tạo mã ID
        String newId = "CD-" + UUID.randomUUID().toString().substring(0, 8);
        chuyenDi.setMaChuyen(newId);

        // 3. Gán các đối tượng
        chuyenDi.setXe(xe);
        chuyenDi.setKhachHang(kh);

        // 4. Gán giờ đón là bây giờ
        chuyenDi.setTgDon(LocalDateTime.now());

        // 5. Lưu (Việc này sẽ kích hoạt trigger trg_validate_trip VÀ
        // trg_update_vehicle_status) [cite: uploaded:schema.sql]
        return chuyenDiRepository.save(chuyenDi);
    }

    /**
     * Hàm 4: Cập nhật thông tin (ví dụ: đổi điểm đến)
     */
    public ChuyenDi updateChuyenDi(String id, ChuyenDi chuyenDiDetails) {
        ChuyenDi chuyenDiHienTai = getChuyenDiById(id);

        // Chỉ cho phép cập nhật vài thông tin cơ bản
        chuyenDiHienTai.setDiemDon(chuyenDiDetails.getDiemDon());
        chuyenDiHienTai.setDiemTra(chuyenDiDetails.getDiemTra());

        return chuyenDiRepository.save(chuyenDiHienTai);
    }

    /**
     * Hàm 5: Xóa một chuyến đi
     */
    public void deleteChuyenDi(String id) {
        ChuyenDi cd = getChuyenDiById(id);
        chuyenDiRepository.delete(cd);
    }

    /**
     * Hàm 6: HOÀN TẤT CHUYẾN ĐI (Quan trọng)
     * Hàm này gọi Stored Procedure [cite: uploaded:schema.sql]
     */
    @Transactional // Đảm bảo CSDL được cập nhật
    public ChuyenDi hoanTatChuyenDi(String id, Double soKmDi) {
        // 1. Gọi Stored Procedure (đã định nghĩa trong Repository)
        chuyenDiRepository.hoanTatChuyenDi(id, soKmDi);

        // 2. Lấy lại dữ liệu đã được SP cập nhật (để trả về cho frontend)
        return getChuyenDiById(id);
    }
}