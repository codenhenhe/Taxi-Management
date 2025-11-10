package com.project.backend.controller;

import com.project.backend.model.BaoTriXe;
import com.project.backend.service.BaoTriXeService; // <-- Gọi Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 1. Báo cho Spring biết đây là API Controller
@RestController
// 2. Tạo đường dẫn URL chung cho class này
@RequestMapping("/api/bao-tri-xe")
// @CrossOrigin(origins = "http://localhost:5173") // (Mở cái này nếu frontend
// bị lỗi CORS)
public class BaoTriXeController {

    // 3. Tiêm (Inject) Service mà bạn vừa viết
    @Autowired
    private BaoTriXeService baoTriXeService;

    // 4. TẠO HÀM API: Lấy tất cả lịch sử bảo trì
    // URL: GET http://localhost:8080/api/bao-tri-xe
    @GetMapping
    public ResponseEntity<List<BaoTriXe>> layTatCaBaoTriXe() {
        List<BaoTriXe> dsBaoTriXe = baoTriXeService.getAllBaoTriXe();
        return ResponseEntity.ok(dsBaoTriXe);
    }

    // 5. TẠO HÀM API: Lấy 1 lịch sử bảo trì theo ID (Mã)
    // URL: GET http://localhost:8080/api/bao-tri-xe/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BaoTriXe> layBaoTriXeTheoId(@PathVariable String id) {
        BaoTriXe bx = baoTriXeService.getBaoTriXeById(id);
        return ResponseEntity.ok(bx);
    }

    // 6. TẠO HÀM API: Tạo lịch sử bảo trì mới (ĐÃ SỬA)
    // URL: POST http://localhost:8080/api/bao-tri-xe?maXe=XE001
    @PostMapping
    public ResponseEntity<BaoTriXe> taoMoiBaoTriXe(
            @RequestBody BaoTriXe baoTriXeMoi,
            @RequestParam String maXe) { // <-- LỖI CỦA BẠN LÀ THIẾU DÒNG NÀY

        // @RequestBody: Chuyển JSON (chi phí, mô tả,...) thành object BaoTriXe
        // @RequestParam: Lấy maXe từ URL

        // Gọi hàm service với 2 tham số (đã sửa)
        BaoTriXe bxMoi = baoTriXeService.createBaoTriXe(baoTriXeMoi, maXe);
        return ResponseEntity.ok(bxMoi);
    }

    // 7. Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<BaoTriXe> capNhatBaoTriXeTheoId(@PathVariable String id,
            @RequestBody BaoTriXe baoTriXeDetails) { // Sửa tên tham số cho rõ ràng

        BaoTriXe baoTriXeCapNhat = baoTriXeService.updateBaoTriXe(id, baoTriXeDetails);
        return ResponseEntity.ok(baoTriXeCapNhat);
    }

    // 8. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaBaoTriXeTheoId(@PathVariable String id) {
        baoTriXeService.deleteBaoTriXe(id);
        return ResponseEntity.noContent().build();
    }
}