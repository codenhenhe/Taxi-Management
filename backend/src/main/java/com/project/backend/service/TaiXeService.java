package com.project.backend.service;

import com.project.backend.model.TaiXe;
import com.project.backend.model.TrangThaiTaiXe; // (Cần import Enum này)
import com.project.backend.repository.TaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // (Cần import để tạo mã)

@Service
public class TaiXeService {

    @Autowired
    private TaiXeRepository taiXeRepository;

    /**
     * Hàm 1: Lấy tất cả tài xế
     * 
     * @return Danh sách tất cả tài xế
     */
    public List<TaiXe> getAllTaiXe() {
        return taiXeRepository.findAll();
    }

    /**
     * Hàm 2: Lấy một tài xế theo ID (Mã TX)
     * 
     * @param id Mã tài xế (ví dụ: 'TX001')
     * @return Một đối tượng TaiXe
     */
    public TaiXe getTaiXeById(String id) {
        Optional<TaiXe> tx = taiXeRepository.findById(id);

        // Sửa lại tin nhắn lỗi cho đúng
        return tx.orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một tài xế mới (TỰ ĐỘNG TẠO MÃ)
     * 
     * @param taiXe Dữ liệu tài xế mới từ Controller
     * @return Tài xế đã được lưu (kèm ID)
     */
    public TaiXe createTaiXe(TaiXe taiXe) {
        // Tự động tạo mã ID
        String newId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        taiXe.setMaTaiXe(newId); // (Giả sử model của bạn có setMaTaiXe)

        // Gán trạng thái mặc định nếu frontend không gửi
        // Trong schema.sql đã có DEFAULT 'Đang làm việc'
        // nhưng chúng ta nên cẩn thận gán 'Rảnh' khi mới tạo
        if (taiXe.getTrangThai() == null) {
            taiXe.setTrangThai(TrangThaiTaiXe.RANH);
        }

        // Gọi hàm save() để lưu vào CSDL
        return taiXeRepository.save(taiXe);
    }

    /**
     * Hàm 4: Cập nhật thông tin tài xế (ĐÃ SỬA VÀ MỞ RỘNG)
     * 
     * @param id           Mã tài xế cần cập nhật
     * @param taiXeDetails Dữ liệu mới
     * @return Tài xế đã được cập nhật
     */
    public TaiXe updateTaiXe(String id, TaiXe taiXeDetails) {
        // 1. Tìm tài xế cũ
        TaiXe taiXeHienTai = getTaiXeById(id); // Tận dụng hàm tìm ở trên

        // 2. Cập nhật thông tin (mở rộng để cập nhật tất cả các trường)
        // (Tất cả các trường này đều có trong TAI_XE)
        taiXeHienTai.setTenTaiXe(taiXeDetails.getTenTaiXe());
        taiXeHienTai.setSdt(taiXeDetails.getSdt());
        taiXeHienTai.setSoHieuGPLX(taiXeDetails.getSoHieuGPLX());
        taiXeHienTai.setNgaySinh(taiXeDetails.getNgaySinh());
        taiXeHienTai.setTrangThai(taiXeDetails.getTrangThai());

        // 3. Lưu lại
        return taiXeRepository.save(taiXeHienTai);
    }

    /**
     * Hàm 5: Xóa một tài xế
     * 
     * @param id Mã tài xế cần xóa
     */
    public void deleteTaiXe(String id) {
        // 1. Tìm tài xế (để chắc chắn nó tồn tại)
        TaiXe tx = getTaiXeById(id);

        // 2. Nếu tìm thấy, thì xóa
        taiXeRepository.delete(tx);
    }
}