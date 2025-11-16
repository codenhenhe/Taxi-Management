package com.project.backend.controller;

import com.project.backend.dto.KetThucPhanCongRequestDTO; // <-- Import
import com.project.backend.dto.PhanCongXeDTO; // <-- Import
import com.project.backend.dto.PhanCongXeRequestDTO; // <-- Import
import com.project.backend.model.PhanCongXeId;
import com.project.backend.service.PhanCongXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page; // <-- 1. Import Page
import org.springframework.data.domain.Pageable; // <-- 2. Import Pageable
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/phan-cong-xe")
public class PhanCongXeController {

    @Autowired
    private PhanCongXeService phanCongXeService;

    // Trả về List<PhanCongXeDTO>
    @GetMapping
    public ResponseEntity<Page<PhanCongXeDTO>> layTatCaPhanCongXe(
            // 1. Thêm các RequestParam cho filter
            @RequestParam(required = false) String maTaiXe,
            @RequestParam(required = false) String maXe,
            @RequestParam(required = false) String tuTGBatDau,
            @RequestParam(required = false) String denTGBatDau,
            @RequestParam(required = false) String tuTGKetThuc,
            @RequestParam(required = false) String denTGKetThuc,
            
            @PageableDefault(size = 10, sort = "id.maTaiXe", direction = Sort.Direction.DESC) Pageable pageable
        ){

            Page<PhanCongXeDTO> dsPhanCongXe = phanCongXeService.getAllPhanCongXe(maTaiXe, maXe, tuTGBatDau, denTGBatDau, tuTGKetThuc, denTGKetThuc, pageable); // <-- 6. Sửa
            return ResponseEntity.ok(dsPhanCongXe);
    }

    // Nhận PhanCongXeRequestDTO, Trả về PhanCongXeDTO
    @PostMapping
    public ResponseEntity<PhanCongXeDTO> taoMoiPhanCongXe(
            @RequestBody PhanCongXeRequestDTO dto) { // <-- Sửa

        PhanCongXeDTO pcxMoi = phanCongXeService.createPhanCongXe(dto); // <-- Sửa
        return ResponseEntity.ok(pcxMoi);
    }

    // Nhận KetThucPhanCongRequestDTO, Trả về PhanCongXeDTO
    @PutMapping("/ket-thuc")
    public ResponseEntity<PhanCongXeDTO> ketThucCaPhanCong(
            @RequestBody KetThucPhanCongRequestDTO dto) { // <-- Sửa

        PhanCongXeDTO phanCongXeCapNhat = phanCongXeService.ketThucPhanCong(dto); // <-- Sửa
        return ResponseEntity.ok(phanCongXeCapNhat);
    }

    // --- CÁC HÀM GET VÀ DELETE BẰNG ID PHỨC HỢP GIỮ NGUYÊN ---
    // (Cách dùng @RequestParam cho GET/DELETE khóa phức hợp là chuẩn rồi)

    // Trả về PhanCongXeDTO
    @GetMapping("/chi-tiet")
    public ResponseEntity<PhanCongXeDTO> layPhanCongXeTheoId( // <-- Sửa kiểu trả về
            @RequestParam String maXe,
            @RequestParam String maTaiXe,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime thoiGianBatDau) {

        PhanCongXeId id = new PhanCongXeId(maTaiXe, maXe, thoiGianBatDau);
        PhanCongXeDTO pcx = phanCongXeService.getPhanCongXeById(id); // <-- Sửa
        return ResponseEntity.ok(pcx);
    }

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