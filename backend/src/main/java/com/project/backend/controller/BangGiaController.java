package com.project.backend.controller;

import com.project.backend.dto.BangGiaDTO; // <-- Import
import com.project.backend.dto.BangGiaRequestDTO; // <-- Import
import com.project.backend.service.BangGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page; // <-- 1. Import Page
import org.springframework.data.domain.Pageable; // <-- 2. Import Pageable
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/bang-gia")
public class BangGiaController {

    @Autowired
    private BangGiaService bangGiaService;

    // Trả về List<BangGiaDTO>
    @GetMapping
    public ResponseEntity<Page<BangGiaDTO>> layTatCaBangGia(
            // 1. Thêm các RequestParam cho filter
            @RequestParam(required = false) String maBangGia,
            @RequestParam(required = false) Double giaKhoiDiem,
            @RequestParam(required = false) Double giaTheoKm,
            @RequestParam(required = false) String maLoai,
            
            @PageableDefault(size = 10, sort = "maBangGia", direction = Sort.Direction.DESC) Pageable pageable
        ){
            Page<BangGiaDTO> dsBangGia = bangGiaService.getAllBangGia(maBangGia, giaKhoiDiem, giaTheoKm, maLoai, pageable); // <-- 6. Sửa
            return ResponseEntity.ok(dsBangGia);
    }

    // Trả về BangGiaDTO
    @GetMapping("/{id}")
    public ResponseEntity<BangGiaDTO> layBangGiaTheoId(@PathVariable String id) {
        BangGiaDTO bg = bangGiaService.getBangGiaById(id);
        return ResponseEntity.ok(bg);
    }

    // Nhận BangGiaRequestDTO, Trả về BangGiaDTO
    @PostMapping
    public ResponseEntity<BangGiaDTO> taoMoiBangGia(@RequestBody BangGiaRequestDTO dto) {
        // Gộp cả 2 tham số (bangGiaMoi + maLoai) vào 1 DTO
        BangGiaDTO bgMoi = bangGiaService.createBangGia(dto);
        return ResponseEntity.ok(bgMoi);
    }

    // Nhận BangGiaRequestDTO, Trả về BangGiaDTO
    @PutMapping("/{id}")
    public ResponseEntity<BangGiaDTO> capNhatBangGiaTheoId(@PathVariable String id,
                                                        @RequestBody BangGiaRequestDTO dto) {
        BangGiaDTO bangGiaCapNhat = bangGiaService.updateBangGia(id, dto);
        return ResponseEntity.ok(bangGiaCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaBangGiaTheoId(@PathVariable String id) {
        bangGiaService.deleteBangGia(id);
        return ResponseEntity.noContent().build();
    }
}