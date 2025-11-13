package com.project.backend.controller;

import com.project.backend.dto.BaoTriXeDTO; // <-- Import
import com.project.backend.dto.BaoTriXeRequestDTO; // <-- Import
import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.service.BaoTriXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bao-tri-xe")
public class BaoTriXeController {

    @Autowired
    private BaoTriXeService baoTriXeService;

    // Trả về List<BaoTriXeDTO>
    @GetMapping
    public ResponseEntity<List<BaoTriXeDTO>> layTatCaBaoTriXe() {
        List<BaoTriXeDTO> dsBaoTriXe = baoTriXeService.getAllBaoTriXe();
        return ResponseEntity.ok(dsBaoTriXe);
    }

    // Trả về BaoTriXeDTO
    @GetMapping("/{id}")
    public ResponseEntity<BaoTriXeDTO> layBaoTriXeTheoId(@PathVariable String id) {
        BaoTriXeDTO bx = baoTriXeService.getBaoTriXeById(id);
        return ResponseEntity.ok(bx);
    }

    // Nhận BaoTriXeRequestDTO, Trả về BaoTriXeDTO
    @PostMapping
    public ResponseEntity<BaoTriXeDTO> taoMoiBaoTriXe(
            @RequestBody BaoTriXeRequestDTO dto) { // <-- Sửa

        BaoTriXeDTO bxMoi = baoTriXeService.createBaoTriXe(dto); // <-- Sửa
        return ResponseEntity.ok(bxMoi);
    }

    // Nhận BaoTriXeRequestDTO, Trả về BaoTriXeDTO
    @PutMapping("/{id}")
    public ResponseEntity<BaoTriXeDTO> capNhatBaoTriXeTheoId(@PathVariable String id,
            @RequestBody BaoTriXeRequestDTO dto) { // <-- Sửa

        BaoTriXeDTO baoTriXeCapNhat = baoTriXeService.updateBaoTriXe(id, dto); // <-- Sửa
        return ResponseEntity.ok(baoTriXeCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaBaoTriXeTheoId(@PathVariable String id) {
        baoTriXeService.deleteBaoTriXe(id);
        return ResponseEntity.noContent().build();
    }

    // --- HÀM THỐNG KÊ (Giữ nguyên, vì đã là DTO) ---
    @GetMapping("/chi-phi-bao-tri")
    public ResponseEntity<List<ThongKePhiBaoTriHangThang>> layChiPhiBaoTri(@RequestParam int year) {
        List<ThongKePhiBaoTriHangThang> stats = baoTriXeService.layThongKeChiPhiBaoTri(year);
        return ResponseEntity.ok(stats);
    }
}