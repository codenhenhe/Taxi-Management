package com.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.backend.model.QuanTriVien;
import com.project.backend.service.QuanTriVienService;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/qtv")
@CrossOrigin(origins = "*")
public class QuanTriVienController {

    @Autowired
    private QuanTriVienService qtvService;

    // Đăng ký
    @PostMapping("/dangky")
    public ResponseEntity<?> dangKy(@RequestBody QuanTriVien qtv) {
        try {
            return ResponseEntity.ok(qtvService.dangKy(qtv));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Đăng nhập
    @GetMapping("/dangnhap")
    public ResponseEntity<?> dangNhap(@RequestParam String tenDangNhap, @RequestParam String matKhau) {
        try {
            String token = qtvService.dangNhap(tenDangNhap, matKhau);
            return ResponseEntity.ok().body(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật thông tin
    @PutMapping("/{maQtv}")
    public ResponseEntity<?> capNhat(@PathVariable String maQtv, @RequestBody QuanTriVien qtvMoi) {
        try {
            return ResponseEntity.ok(qtvService.capNhatThongTin(maQtv, qtvMoi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET ALL (yêu cầu token)
    @GetMapping
    public ResponseEntity<List<QuanTriVien>> getAll() {
        return ResponseEntity.ok(qtvService.getAll());
    }

    // GET ONE (yêu cầu token)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(qtvService.getOne(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
