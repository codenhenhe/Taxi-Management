package com.project.backend.service;

import com.project.backend.model.BangGia;
import com.project.backend.model.LoaiXe; // Cần import LoaiXe
import com.project.backend.repository.BangGiaRepository;
import com.project.backend.repository.LoaiXeRepository; // Cần Repository của LoaiXe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Thêm import này

@Service
public class BangGiaService {

    @Autowired
    private BangGiaRepository bangGiaRepository; // Sửa 1: Dùng đúng Repository

    @Autowired
    private LoaiXeRepository loaiXeRepository; // Sửa 2: Dùng LoaiXeRepository

    /**
     * Hàm 1: Lấy tất cả bảng giá
     * 
     * @return Danh sách tất cả bảng giá
     */
    public List<BangGia> getAllBangGia() {
        return bangGiaRepository.findAll(); // Sửa 3: Dùng đúng Repository
    }

    /**
     * Hàm 2: Lấy một bảng giá theo ID (Mã BG)
     * 
     * @param id Mã bảng giá (ví dụ: 'G001')
     * @return Một đối tượng BangGia
     */
    public BangGia getBangGiaById(String id) {
        Optional<BangGia> bg = bangGiaRepository.findById(id); // Sửa 4

        // Sửa lại lời nhắn lỗi
        return bg.orElseThrow(() -> new RuntimeException("Không tìm thấy bảng giá với ID: " + id)); // Sửa 5
    }

    /**
     * Hàm 3: Tạo một bảng giá mới (TỰ ĐỘNG TẠO MÃ)
     * 
     * @param bangGia Dữ liệu bảng giá mới từ Controller
     * @param maLoai  Mã của loại xe
     * @return Bảng giá đã được lưu
     */
    public BangGia createBangGia(BangGia bangGia, String maLoai) { // Sửa 6
        // 1. Tìm loại xe tương ứng
        LoaiXe loaiXe = loaiXeRepository.findById(maLoai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại xe với ID: " + maLoai));

        // 2. Tự động tạo mã ID
        String newId = "BG-" + UUID.randomUUID().toString().substring(0, 8);
        bangGia.setMaBangGia(newId); // Sửa 7

        // 3. Gán loại xe này vào bảng giá
        bangGia.setLoaiXe(loaiXe);

        // 4. Lưu vào CSDL
        return bangGiaRepository.save(bangGia); // Sửa 8
    }

    /**
     * Hàm 4: Cập nhật thông tin bảng giá
     * 
     * @param id             Mã bảng giá cần cập nhật
     * @param bangGiaDetails Dữ liệu mới
     * @return Bảng giá đã được cập nhật
     */
    public BangGia updateBangGia(String id, BangGia bangGiaDetails) { // Sửa 9
        // 1. Tìm bảng giá cũ
        BangGia bangGiaHienTai = getBangGiaById(id);

        // 2. Cập nhật thông tin (Sửa lại cho đúng các trường của BangGia)
        // (Theo schema.sql: gia_khoi_diem, gia_theo_km, phu_thu)
        bangGiaHienTai.setGiaKhoiDiem(bangGiaDetails.getGiaKhoiDiem());
        bangGiaHienTai.setGiaTheoKm(bangGiaDetails.getGiaTheoKm());
        bangGiaHienTai.setPhuThu(bangGiaDetails.getPhuThu());
        // (Không cho phép cập nhật ma_loai ở đây, nếu cần thì phải làm phức tạp hơn)

        // 3. Lưu lại
        return bangGiaRepository.save(bangGiaHienTai); // Sửa 10
    }

    /**
     * Hàm 5: Xóa một bảng giá
     * 
     * @param id Mã bảng giá cần xóa
     */
    public void deleteBangGia(String id) { // Sửa 11
        // 1. Tìm (để chắc chắn nó tồn tại)
        BangGia bg = getBangGiaById(id);

        // 2. Nếu tìm thấy, thì xóa
        bangGiaRepository.delete(bg); // Sửa 12
    }
}