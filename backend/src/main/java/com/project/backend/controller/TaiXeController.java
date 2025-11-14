package com.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.backend.dto.RevenueByDriver;
import com.project.backend.dto.TaiXeDTO; // <-- Import
import com.project.backend.dto.TaiXeRequestDTO; // <-- Import
import com.project.backend.service.TaiXeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tai-xe")
public class TaiXeController {

    @Autowired
    private TaiXeService taiXeService;

    // Trả về List<TaiXeDTO>
    @GetMapping // <-- Sửa thành thế này
    public ResponseEntity<List<TaiXeDTO>> layTatCaTaiXe() {
        List<TaiXeDTO> dsTaiXe = taiXeService.getAllTaiXe();
        return ResponseEntity.ok(dsTaiXe);
    }

    // Trả về TaiXeDTO
    @GetMapping("/{id}")
    public ResponseEntity<TaiXeDTO> layTaiXeTheoId(@PathVariable String id) {
        TaiXeDTO tx = taiXeService.getTaiXeById(id); // <-- Sửa
        return ResponseEntity.ok(tx);
    }

    // Nhận TaiXeRequestDTO, Trả về TaiXeDTO
    @PostMapping
    public ResponseEntity<TaiXeDTO> taoMoiTaiXe(@RequestBody TaiXeRequestDTO taiXeMoi) { // <-- Sửa
        TaiXeDTO txMoi = taiXeService.createTaiXe(taiXeMoi); // <-- Sửa
        return ResponseEntity.ok(txMoi);
    }

    // Nhận TaiXeRequestDTO, Trả về TaiXeDTO
    @PutMapping("/{id}")
    public ResponseEntity<TaiXeDTO> capNhatTaiXeTheoId(@PathVariable String id,
            @RequestBody TaiXeRequestDTO taiXeMoi) { // <-- Sửa
        TaiXeDTO taiXeCapNhat = taiXeService.updateTaiXe(id, taiXeMoi); // <-- Sửa
        return ResponseEntity.ok(taiXeCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaTaiXeTheoId(@PathVariable String id) {
        taiXeService.deleteTaiXe(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINT NÀY GIỮ NGUYÊN (VÌ ĐÃ DÙNG DTO) ---
    // URL: GET http://localhost:8080/api/tai-xe/doanh-thu-tai-xe?date=2025-05-05
    @GetMapping("/doanh-thu-tai-xe")
    public ResponseEntity<List<RevenueByDriver>> layDoanhThuTaiXe(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RevenueByDriver> stats = taiXeService.layDoanhThuTheoTaiXe(date);
        return ResponseEntity.ok(stats);
    }
}