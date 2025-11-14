package com.project.backend.controller;

import com.project.backend.model.BangGia;
import com.project.backend.service.BangGiaService; // <-- Gọi Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 1. Báo cho Spring biết đây là API Controller
@RestController
// 2. Tạo đường dẫn URL chung cho class này
@RequestMapping("/api/bang-gia")
// @CrossOrigin(origins = "http://localhost:5173")
public class BangGiaController {

    // 3. Tiêm (Inject) Service mà bạn vừa viết
    @Autowired
    private BangGiaService bangGiaService;

    // 4. TẠO HÀM API: Lấy tất cả bảng giá
    // URL: GET http://localhost:8080/api/bang-gia
    @GetMapping
    public ResponseEntity<List<BangGia>> layTatCaBangGia() {
        List<BangGia> dsBangGia = bangGiaService.getAllBangGia();
        return ResponseEntity.ok(dsBangGia);
    }

    // 5. TẠO HÀM API: Lấy 1 bảng giá theo ID (Mã)
    // URL: GET http://localhost:8080/api/bang-gia/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BangGia> layBangGiaTheoId(@PathVariable String id) {
        BangGia bg = bangGiaService.getBangGiaById(id);
        return ResponseEntity.ok(bg);
    }

    // 6. TẠO HÀM API: Tạo bảng giá mới (ĐÃ SỬA)
    // URL: POST http://localhost:8080/api/bang-gia?maLoai=LX001
    @PostMapping
    public ResponseEntity<BangGia> taoMoiBangGia(
            @RequestBody BangGia bangGiaMoi,
            @RequestParam String maLoai) { // <-- LỖI CỦA BẠN LÀ THIẾU DÒNG NÀY

        // @RequestBody: Tự động chuyển JSON từ frontend thành object BangGia
        // @RequestParam: Lấy maLoai từ URL

        // Gọi hàm service với 2 tham số
        BangGia bgMoi = bangGiaService.createBangGia(bangGiaMoi, maLoai);
        return ResponseEntity.ok(bgMoi);
    }

    // 7. Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<BangGia> capNhatBangGiaTheoId(@PathVariable String id,
            @RequestBody BangGia bangGiaDetails) { // Sửa tên tham số cho rõ ràng

        BangGia bangGiaCapNhat = bangGiaService.updateBangGia(id, bangGiaDetails);
        return ResponseEntity.ok(bangGiaCapNhat);
    }

    // 8. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaBangGiaTheoId(@PathVariable String id) {
        bangGiaService.deleteBangGia(id);
        return ResponseEntity.noContent().build();
    }
}