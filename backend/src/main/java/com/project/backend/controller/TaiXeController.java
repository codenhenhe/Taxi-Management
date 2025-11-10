package com.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.backend.model.TaiXe;
import com.project.backend.service.TaiXeService;

import java.util.List;

// 1. Báo cho Spring biết đây là API Controller
@RestController
// 2. Tạo đường dẫn URL chung cho class này
@RequestMapping("/api/tai-xe")
// @CrossOrigin(origins = "http://localhost:5173") // (Mở cái này nếu frontend
// bị lỗi CORS)
public class TaiXeController {

    // 3. Tiêm (Inject) Service mà bạn vừa viết
    @Autowired
    private TaiXeService taiXeService;

    // 4. TẠO HÀM API: Lấy tất cả khách hàng
    // URL: GET http://localhost:8080/api/tai-xe
    @GetMapping
    public ResponseEntity<List<TaiXe>> layTatCaTaiXe() {
        // Gọi hàm service
        List<TaiXe> dsTaiXe = taiXeService.getAllTaiXe();
        // Trả về cho frontend
        return ResponseEntity.ok(dsTaiXe);
    }

    // 5. TẠO HÀM API: Lấy 1 tài xế theo ID (Mã)
    // URL: GET http://localhost:8080/api/tai-xe/TX001
    @GetMapping("/{id}")
    public ResponseEntity<TaiXe> layTaiXeTheoId(@PathVariable String id) {
        TaiXe tx = taiXeService.getTaiXeById(id); // (Bạn cần tự viết hàm này trong Service)
        return ResponseEntity.ok(tx);
    }

    // 6. TẠO HÀM API: Tạo tài xế mới
    // URL: POST http://localhost:8080/api/tai-xe
    @PostMapping
    public ResponseEntity<TaiXe> taoMoiTaiXe(@RequestBody TaiXe taiXeMoi) {
        // @RequestBody: Tự động chuyển JSON từ frontend thành object TaiXe
        // (Dữ liệu này được định nghĩa trong schema.sql)
        TaiXe txMoi = taiXeService.createTaiXe(taiXeMoi); // (Bạn cần tự viết hàm này trong Service)
        return ResponseEntity.ok(txMoi);
    }

    // ... Tạo thêm các hàm @PutMapping (cập nhật) và @DeleteMapping (xóa) ...
    @PutMapping("/{id}")
    public ResponseEntity<TaiXe> capNhatTaiXeTheoId(@PathVariable String id,
            @RequestBody TaiXe taiXeMoi) {
        TaiXe taiXeCapNhat = taiXeService.updateTaiXe(id, taiXeMoi);
        return ResponseEntity.ok(taiXeCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaTaiXeTheoId(@PathVariable String id) {
        taiXeService.deleteTaiXe(id);
        return ResponseEntity.noContent().build();
    }
}