package com.project.backend.service;

import com.project.backend.model.KhachHang;
import com.project.backend.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// 1. Đánh dấu đây là một Class Service
@Service
public class KhachHangService {

    // 2. Tiêm (Inject) KhachHangRepository để làm việc với CSDL
    @Autowired
    private KhachHangRepository khachHangRepository;

    // --- CÁC HÀM BẠN ĐÃ ĐỊNH NGHĨA BÊN CONTROLLER ---

    /**
     * Hàm 1: Lấy tất cả khách hàng
     * 
     * @return Danh sách tất cả khách hàng
     */
    public List<KhachHang> getAllKhachHang() {
        // Chỉ cần gọi hàm findAll() của Repository
        return khachHangRepository.findAll();
    }

    /**
     * Hàm 2: Lấy một khách hàng theo ID (Mã KH)
     * 
     * @param id Mã khách hàng (ví dụ: 'KH001')
     * @return Một đối tượng KhachHang
     */
    public KhachHang getKhachHangById(String id) {
        // findById trả về một Optional (có thể có hoặc không)
        Optional<KhachHang> kh = khachHangRepository.findById(id);

        // Nếu tìm thấy, trả về khách hàng.
        // Nếu không, ném ra lỗi (Controller sẽ bắt lỗi này)
        return kh.orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một khách hàng mới
     * 
     * @param khachHang Dữ liệu khách hàng mới từ Controller
     * @return Khách hàng đã được lưu (kèm ID)
     */
    public KhachHang createKhachHang(KhachHang khachHang) {
        // (Đây là nơi bạn có thể thêm logic kiểm tra
        // ví dụ: kiểm tra SĐT đã tồn tại chưa)

        // Gọi hàm save() để lưu vào CSDL
        return khachHangRepository.save(khachHang);
    }

    // --- CÁC HÀM CRUD MỞ RỘNG (NÊN CÓ) ---

    /**
     * Hàm 4: Cập nhật thông tin khách hàng
     * 
     * @param id               Mã khách hàng cần cập nhật
     * @param khachHangDetails Dữ liệu mới
     * @return Khách hàng đã được cập nhật
     */
    public KhachHang updateKhachHang(String id, KhachHang khachHangDetails) {
        // 1. Tìm khách hàng cũ
        KhachHang khachHangHienTai = getKhachHangById(id); // Tận dụng hàm tìm ở trên

        // 2. Cập nhật thông tin
        khachHangHienTai.setTenKhachHang(khachHangDetails.getTenKhachHang());
        khachHangHienTai.setSdt(khachHangDetails.getSdt());

        // 3. Lưu lại
        return khachHangRepository.save(khachHangHienTai);
    }

    /**
     * Hàm 5: Xóa một khách hàng
     * 
     * @param id Mã khách hàng cần xóa
     */
    public void deleteKhachHang(String id) {
        // 1. Tìm khách hàng (để chắc chắn nó tồn tại)
        KhachHang kh = getKhachHangById(id);

        // 2. Nếu tìm thấy, thì xóa
        khachHangRepository.delete(kh);
    }
}