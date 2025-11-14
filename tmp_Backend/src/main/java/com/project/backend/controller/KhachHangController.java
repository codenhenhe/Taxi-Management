package com.project.backend.controller;

import com.project.backend.model.KhachHang;
import com.project.backend.service.KhachHangService; // <-- Gọi Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 1. Báo cho Spring biết đây là API Controller
@RestController
// 2. Tạo đường dẫn URL chung cho class này
@RequestMapping("/api/khach-hang")
// @CrossOrigin(origins = "http://localhost:5173") // (Mở cái này nếu frontend
// bị lỗi CORS)
public class KhachHangController {

    // 3. Tiêm (Inject) Service mà bạn vừa viết
    @Autowired
    private KhachHangService khachHangService;

    // 4. TẠO HÀM API: Lấy tất cả khách hàng
    // URL: GET http://localhost:8080/api/khach-hang
    @GetMapping
    public ResponseEntity<List<KhachHang>> layTatCaKhachHang() {
        // Gọi hàm service
        List<KhachHang> dsKhachHang = khachHangService.getAllKhachHang();
        // Trả về cho frontend
        return ResponseEntity.ok(dsKhachHang);
    }

    // 5. TẠO HÀM API: Lấy 1 khách hàng theo ID (Mã)
    // URL: GET http://localhost:8080/api/khach-hang/KH001
    @GetMapping("/{id}")
    public ResponseEntity<KhachHang> layKhachHangTheoId(@PathVariable String id) {
        KhachHang kh = khachHangService.getKhachHangById(id); // (Bạn cần tự viết hàm này trong Service)
        return ResponseEntity.ok(kh);
    }

    // 6. TẠO HÀM API: Tạo khách hàng mới
    // URL: POST http://localhost:8080/api/khach-hang
    @PostMapping
    public ResponseEntity<KhachHang> taoMoiKhachHang(@RequestBody KhachHang khachHangMoi) {
        // @RequestBody: Tự động chuyển JSON từ frontend thành object KhachHang
        // (Dữ liệu này được định nghĩa trong schema.sql)
        KhachHang khMoi = khachHangService.createKhachHang(khachHangMoi); // (Bạn cần tự viết hàm này trong Service)
        return ResponseEntity.ok(khMoi);
    }

    // ... Tạo thêm các hàm @PutMapping (cập nhật) và @DeleteMapping (xóa) ...
    @PutMapping("/{id}")
    public ResponseEntity<KhachHang> capNhatKhachHangTheoId(@PathVariable String id, @RequestBody KhachHang khachHangMoi) {
        KhachHang khachHangCapNhat = khachHangService.updateKhachHang(id, khachHangMoi);
        return ResponseEntity.ok(khachHangCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaKhachHangTheoId(@PathVariable String id) {
        khachHangService.deleteKhachHang(id);
        return ResponseEntity.noContent().build();
    }
}