package com.project.backend.controller;

import com.project.backend.model.PhanCongXe;
import com.project.backend.model.PhanCongXeId;
import com.project.backend.service.PhanCongXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/phan-cong-xe")
// @CrossOrigin(origins = "http://localhost:5173")
public class PhanCongXeController {

    @Autowired
    private PhanCongXeService phanCongXeService;

    // URL: GET http://localhost:8080/api/phan-cong-xe
    @GetMapping
    public ResponseEntity<List<PhanCongXe>> layTatCaPhanCongXe() {
        List<PhanCongXe> dsPhanCongXe = phanCongXeService.getAllPhanCongXe();
        return ResponseEntity.ok(dsPhanCongXe);
    }

    // URL: POST http://localhost:8080/api/phan-cong-xe
    // (Gửi thoiGianBatDau là tùy chọn)
    @PostMapping
    public ResponseEntity<PhanCongXe> taoMoiPhanCongXe(
            @RequestParam String maXe,
            @RequestParam String maTaiXe,
            // Thêm tham số này, nhưng không bắt buộc (required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Báo Spring cách đọc
                                                                                                // chuỗi ngày giờ
            LocalDateTime thoiGianBatDau) {

        // Chuyển cả 3 tham số (thoiGianBatDau có thể null) vào Service
        PhanCongXe pcxMoi = phanCongXeService.createPhanCongXe(maXe, maTaiXe, thoiGianBatDau);
        return ResponseEntity.ok(pcxMoi);
    }

    // URL: PUT http://localhost:8080/api/phan-cong-xe/ket-thuc?maTaiXe=TX001
    @PutMapping("/ket-thuc")
    public ResponseEntity<PhanCongXe> ketThucCaPhanCong(@RequestParam String maTaiXe) {
        PhanCongXe phanCongXeCapNhat = phanCongXeService.ketThucPhanCong(maTaiXe);
        return ResponseEntity.ok(phanCongXeCapNhat);
    }

    // URL: GET
    // http://localhost:8080/api/phan-cong-xe/chi-tiet?maXe=...&maTaiXe=...&thoiGianBatDau=...
    @GetMapping("/chi-tiet")
    public ResponseEntity<PhanCongXe> layPhanCongXeTheoId(
            @RequestParam String maXe,
            @RequestParam String maTaiXe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thoiGianBatDau) {

        PhanCongXeId id = new PhanCongXeId(maTaiXe, maXe, thoiGianBatDau);
        PhanCongXe pcx = phanCongXeService.getPhanCongXeById(id);
        return ResponseEntity.ok(pcx);
    }

    // URL: DELETE
    // http://localhost:8080/api/phan-cong-xe?maXe=...&maTaiXe=...&thoiGianBatDau=...
    @DeleteMapping
    public ResponseEntity<Void> xoaPhanCongXe(
            @RequestParam String maXe,
            @RequestParam String maTaiXe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thoiGianBatDau) {

        PhanCongXeId id = new PhanCongXeId(maTaiXe, maXe, thoiGianBatDau);
        phanCongXeService.deletePhanCongXe(id);
        return ResponseEntity.noContent().build();
    }
}