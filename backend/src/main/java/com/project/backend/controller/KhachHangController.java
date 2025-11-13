package com.project.backend.controller;

import com.project.backend.dto.KhachHangDTO; // <-- Import DTO
import com.project.backend.dto.KhachHangRequestDTO; // <-- Import DTO
import com.project.backend.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    // Trả về List<KhachHangDTO>
    @GetMapping
    public ResponseEntity<List<KhachHangDTO>> layTatCaKhachHang() {
        List<KhachHangDTO> dsKhachHang = khachHangService.getAllKhachHang();
        return ResponseEntity.ok(dsKhachHang);
    }

    // Trả về KhachHangDTO
    @GetMapping("/{id}")
    public ResponseEntity<KhachHangDTO> layKhachHangTheoId(@PathVariable String id) {
        KhachHangDTO kh = khachHangService.getKhachHangById(id);
        return ResponseEntity.ok(kh);
    }

    // Nhận vào KhachHangRequestDTO, Trả về KhachHangDTO
    @PostMapping
    public ResponseEntity<KhachHangDTO> taoMoiKhachHang(@RequestBody KhachHangRequestDTO khachHangMoi) {
        KhachHangDTO khMoi = khachHangService.createKhachHang(khachHangMoi);
        return ResponseEntity.ok(khMoi);
    }

    // Nhận vào KhachHangRequestDTO, Trả về KhachHangDTO
    @PutMapping("/{id}")
    public ResponseEntity<KhachHangDTO> capNhatKhachHangTheoId(@PathVariable String id,
            @RequestBody KhachHangRequestDTO khachHangMoi) {
        KhachHangDTO khachHangCapNhat = khachHangService.updateKhachHang(id, khachHangMoi);
        return ResponseEntity.ok(khachHangCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaKhachHangTheoId(@PathVariable String id) {
        khachHangService.deleteKhachHang(id);
        return ResponseEntity.noContent().build();
    }
}