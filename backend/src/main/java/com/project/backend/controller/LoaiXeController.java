package com.project.backend.controller;

import com.project.backend.dto.LoaiXeDTO; // <-- Sửa
import com.project.backend.dto.LoaiXeRequestDTO; // <-- Thêm
import com.project.backend.service.LoaiXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page; // <-- 1. Import Page
import org.springframework.data.domain.Pageable; // <-- 2. Import Pageable
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort; 

import java.util.List;

@RestController
@RequestMapping("/api/loai-xe")
public class LoaiXeController {

    @Autowired
    private LoaiXeService loaiXeService;

    // URL: GET http://localhost:8080/api/loai-xe
    @GetMapping
    public ResponseEntity<Page<LoaiXeDTO>> layTatCaLoaiXe(
            // 1. Thêm các RequestParam cho filter
            @RequestParam(required = false) String maLoai,
            @RequestParam(required = false) String tenLoai,
            @RequestParam(required = false) String soGhe,
            
            // Frontend sẽ gửi: ?page=0&size=10&sort=tenLoai,asc
            @PageableDefault(size = 10, sort = "maLoai", direction = Sort.Direction.DESC) Pageable pageable
        ){
            Page<LoaiXeDTO> dsLoaiXe = loaiXeService.getAllLoaiXe(maLoai, tenLoai, soGhe, pageable); // <-- 6. Sửa
            return ResponseEntity.ok(dsLoaiXe);
    }

    // URL: GET http://localhost:8080/api/loai-xe/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LoaiXeDTO> layLoaiXeTheoId(@PathVariable String id) { // <-- Sửa
        LoaiXeDTO lx = loaiXeService.getLoaiXeById(id); // <-- Sửa
        return ResponseEntity.ok(lx);
    }

    // URL: POST http://localhost:8080/api/loai-xe
    @PostMapping
    public ResponseEntity<LoaiXeDTO> taoMoiLoaiXe(@RequestBody LoaiXeRequestDTO dto) { // <-- Sửa
        LoaiXeDTO lxMoi = loaiXeService.createLoaiXe(dto); // <-- Sửa
        return ResponseEntity.ok(lxMoi);
    }

    // URL: PUT http://localhost:8080/api/loai-xe/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LoaiXeDTO> capNhatLoaiXeTheoId(@PathVariable String id,
            @RequestBody LoaiXeRequestDTO dto) { // <-- Sửa

        LoaiXeDTO lxCapNhat = loaiXeService.updateLoaiXe(id, dto); // <-- Sửa
        return ResponseEntity.ok(lxCapNhat);
    }

    // URL: DELETE http://localhost:8080/api/loai-xe/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaLoaiXeTheoId(@PathVariable String id) {
        loaiXeService.deleteLoaiXe(id);
        return ResponseEntity.noContent().build();
    }
}