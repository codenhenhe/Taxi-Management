package com.project.backend.service;

import com.project.backend.model.LoaiXe;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Thêm import này

// 1. Đánh dấu đây là một Class Service
@Service
public class LoaiXeService {

    // 2. Tiêm (Inject) LoaiXeRepository để làm việc với CSDL
    @Autowired
    private LoaiXeRepository loaiXeRepository;

    /**
     * Hàm 1: Lấy tất cả loại xe
     * 
     * @return Danh sách tất cả loại xe
     */
    public List<LoaiXe> getAllLoaiXe() {
        return loaiXeRepository.findAll();
    }

    /**
     * Hàm 2: Lấy một loại xe theo ID (Mã Loai)
     * 
     * @param id Mã loại xe (ví dụ: 'LX001')
     * @return Một đối tượng LoaiXe
     */
    public LoaiXe getLoaiXeById(String id) {
        Optional<LoaiXe> lx = loaiXeRepository.findById(id);

        // Sửa lại tin nhắn lỗi
        return lx.orElseThrow(() -> new RuntimeException("Không tìm thấy loại xe với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một loại xe mới (TỰ ĐỘNG TẠO MÃ)
     * 
     * @param loaiXe Dữ liệu loại xe mới từ Controller
     * @return Loại xe đã được lưu (kèm ID)
     */
    public LoaiXe createLoaiXe(LoaiXe loaiXe) {
        // Tự động tạo mã ID
        String newId = "LX-" + UUID.randomUUID().toString().substring(0, 8);
        loaiXe.setMaLoai(newId); // Giả sử model của bạn có 'maLoai'

        // Gọi hàm save() để lưu vào CSDL
        return loaiXeRepository.save(loaiXe);
    }

    /**
     * Hàm 4: Cập nhật thông tin loại xe (ĐÃ SỬA)
     * 
     * @param id            Mã loại xe cần cập nhật
     * @param loaiXeDetails Dữ liệu mới
     * @return Loại xe đã được cập nhật
     */
    public LoaiXe updateLoaiXe(String id, LoaiXe loaiXeDetails) {
        // 1. Tìm loại xe cũ
        LoaiXe loaiXeHienTai = getLoaiXeById(id); // Tận dụng hàm tìm ở trên

        // 2. Cập nhật thông tin (Sửa lại cho đúng)
        // LoaiXe chỉ có 'ten_loai'
        // (Giả sử model của bạn có 'tenLoai')
        loaiXeHienTai.setTenLoai(loaiXeDetails.getTenLoai());

        // 3. Lưu lại
        return loaiXeRepository.save(loaiXeHienTai);
    }

    /**
     * Hàm 5: Xóa một loại xe
     * 
     * @param id Mã loại xe cần xóa
     */
    public void deleteLoaiXe(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        LoaiXe lx = getLoaiXeById(id);

        // 2. Nếu tìm thấy, thì xóa
        loaiXeRepository.delete(lx);
    }
}