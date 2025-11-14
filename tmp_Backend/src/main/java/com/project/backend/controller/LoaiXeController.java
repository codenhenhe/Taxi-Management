package com.project.backend.controller;

import com.project.backend.model.LoaiXe;
import com.project.backend.service.LoaiXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loai-xe") // <-- SỬA LẠI ĐƯỜNG DẪN NÀY (lỗi của bạn là ở đây)
// @CrossOrigin(origins = "http://localhost:5173")
public class LoaiXeController {

    @Autowired
    private LoaiXeService loaiXeService;

    // URL: GET http://localhost:8080/api/loai-xe
    @GetMapping
    public ResponseEntity<List<LoaiXe>> layTatCaLoaiXe() {
        List<LoaiXe> dsLoaiXe = loaiXeService.getAllLoaiXe();
        return ResponseEntity.ok(dsLoaiXe);
    }

    // URL: GET http://localhost:8080/api/loai-xe/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LoaiXe> layLoaiXeTheoId(@PathVariable String id) {
        LoaiXe lx = loaiXeService.getLoaiXeById(id);
        return ResponseEntity.ok(lx);
    }

    // URL: POST http://localhost:8080/api/loai-xe
    @PostMapping
    public ResponseEntity<LoaiXe> taoMoiLoaiXe(@RequestBody LoaiXe loaiXeMoi) {
        // (Service sẽ tự tạo mã LX-)
        LoaiXe lxMoi = loaiXeService.createLoaiXe(loaiXeMoi);
        return ResponseEntity.ok(lxMoi);
    }

    // URL: PUT http://localhost:8080/api/loai-xe/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LoaiXe> capNhatLoaiXeTheoId(@PathVariable String id,
            @RequestBody LoaiXe loaiXeDetails) {

        LoaiXe lxCapNhat = loaiXeService.updateLoaiXe(id, loaiXeDetails);
        return ResponseEntity.ok(lxCapNhat);
    }

    // URL: DELETE http://localhost:8080/api/loai-xe/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaLoaiXeTheoId(@PathVariable String id) {
        loaiXeService.deleteLoaiXe(id);
        return ResponseEntity.noContent().build();
    }
}