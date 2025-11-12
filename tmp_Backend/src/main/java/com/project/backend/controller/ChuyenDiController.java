package com.project.backend.controller;

import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.model.ChuyenDi;
import com.project.backend.service.ChuyenDiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chuyen-di")
// @CrossOrigin(origins = "http://localhost:5173")
public class ChuyenDiController {

    @Autowired
    private ChuyenDiService chuyenDiService;

    // URL: GET http://localhost:8080/api/chuyen-di (Sửa lại comment)
    @GetMapping
    public ResponseEntity<List<ChuyenDi>> layTatCaChuyenDi() {
        List<ChuyenDi> dsChuyenDi = chuyenDiService.getAllChuyenDi();
        return ResponseEntity.ok(dsChuyenDi);
    }

    // URL: GET http://localhost:8080/api/chuyen-di/{id} (Sửa lại comment)
    @GetMapping("/{id}")
    public ResponseEntity<ChuyenDi> layChuyenDiTheoId(@PathVariable String id) {
        ChuyenDi cd = chuyenDiService.getChuyenDiById(id);
        return ResponseEntity.ok(cd);
    }

    // URL: POST http://localhost:8080/api/chuyen-di?maXe=XE001&maKhachHang=KH001
    // (Sửa lại hàm POST để nhận ID)
    @PostMapping
    public ResponseEntity<ChuyenDi> taoMoiChuyenDi(
            @RequestBody ChuyenDi chuyenDiMoi,
            @RequestParam String maXe,
            @RequestParam String maKhachHang) {

        // Frontend chỉ cần gửi JSON chứa: {"diemDon": "...", "diemTra": "..."}
        ChuyenDi cdMoi = chuyenDiService.createChuyenDi(chuyenDiMoi, maXe, maKhachHang);
        return ResponseEntity.ok(cdMoi);
    }

    // URL: PUT http://localhost:8080/api/chuyen-di/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ChuyenDi> capNhatChuyenDiTheoId(@PathVariable String id,
            @RequestBody ChuyenDi chuyenDiMoi) {
        ChuyenDi chuyenDiCapNhat = chuyenDiService.updateChuyenDi(id, chuyenDiMoi);
        return ResponseEntity.ok(chuyenDiCapNhat);
    }

    // URL: DELETE http://localhost:8080/api/chuyen-di/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaChuyenDiTheoId(@PathVariable String id) {
        chuyenDiService.deleteChuyenDi(id);
        return ResponseEntity.noContent().build();
    }

    // --- HÀM MỚI QUAN TRỌNG ĐỂ HOÀN TẤT CHUYẾN ---
    // URL: PUT http://localhost:8080/api/chuyen-di/{id}/hoan-tat
    @PutMapping("/hoan-tat/{maChuyen}")
    public ResponseEntity<Void> hoanTatChuyen(
            @PathVariable String maChuyen,
            @RequestParam Double soKm) {
        chuyenDiService.hoanTatChuyenDi(maChuyen, soKm);
        return ResponseEntity.ok().build();
    }
    // 
    @GetMapping("/thong-ke-chuyen-theo-gio")
    public ResponseEntity<List<ThongKeChuyenTheoGio>> layThongKe() {
        List<ThongKeChuyenTheoGio> thongKe = chuyenDiService.layThongKeChuyenTheoGio();
        return ResponseEntity.ok(thongKe);
    }
    
}