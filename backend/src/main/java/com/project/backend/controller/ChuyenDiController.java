package com.project.backend.controller;

import com.project.backend.dto.ChuyenDiDTO; // <-- Import
import com.project.backend.dto.ChuyenDiRequestDTO; // <-- Import
import com.project.backend.dto.HoanTatChuyenDiRequestDTO; // <-- Import
import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.service.ChuyenDiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page; // <-- 1. Import Page
import org.springframework.data.domain.Pageable; // <-- 2. Import Pageable
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/chuyen-di")
public class ChuyenDiController {

    @Autowired
    private ChuyenDiService chuyenDiService;

    // Trả về List<ChuyenDiDTO>
    @GetMapping
    public ResponseEntity<Page<ChuyenDiDTO>> layTatCaChuyenDi(
            // 1. Thêm các RequestParam cho filter
            @RequestParam(required = false) String maChuyen,
            @RequestParam(required = false) String diemDon,
            @RequestParam(required = false) String diemTra,
            @RequestParam(required = false) String tuNgayDon,
            @RequestParam(required = false) String denNgayDon,
            @RequestParam(required = false) String tuNgayTra,
            @RequestParam(required = false) String denNgayTra,
            @RequestParam(required = false) Double soKmDi,
            @RequestParam(required = false) Double cuocPhi,
            @RequestParam(required = false) String maXe,
            @RequestParam(required = false) String maKhachHang,
            
            @PageableDefault(size = 10, sort = "maChuyen", direction = Sort.Direction.DESC) Pageable pageable
        ){

            Page<ChuyenDiDTO> dsChuyenDi = chuyenDiService.getAllChuyenDi(maChuyen, diemDon, diemTra, tuNgayDon, denNgayDon, tuNgayTra, denNgayTra, soKmDi, cuocPhi, maXe, maKhachHang, pageable); // <-- 6. Sửa
            return ResponseEntity.ok(dsChuyenDi);
    }

    // Trả về ChuyenDiDTO
    @GetMapping("/{id}")
    public ResponseEntity<ChuyenDiDTO> layChuyenDiTheoId(@PathVariable String id) {
        ChuyenDiDTO cd = chuyenDiService.getChuyenDiById(id);
        return ResponseEntity.ok(cd);
    }

    // Nhận ChuyenDiRequestDTO, Trả về ChuyenDiDTO
    @PostMapping
    public ResponseEntity<ChuyenDiDTO> taoMoiChuyenDi(@RequestBody ChuyenDiRequestDTO dto) {
        // Gộp (diemDon, diemTra, maXe, maKhachHang) vào 1 DTO
        System.out.println("Trước tạo mới chuyến đi: ");
        ChuyenDiDTO cdMoi = chuyenDiService.createChuyenDi(dto);
        System.out.println("Sau tạo mới chuyến đi: ");

        return ResponseEntity.ok(cdMoi);
    }

    // Nhận ChuyenDiRequestDTO, Trả về ChuyenDiDTO
    @PutMapping("/{id}")
    public ResponseEntity<ChuyenDiDTO> capNhatChuyenDiTheoId(@PathVariable String id,
            @RequestBody ChuyenDiRequestDTO dto) {
        ChuyenDiDTO chuyenDiCapNhat = chuyenDiService.updateChuyenDi(id, dto);
        return ResponseEntity.ok(chuyenDiCapNhat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaChuyenDiTheoId(@PathVariable String id) {
        chuyenDiService.deleteChuyenDi(id);
        return ResponseEntity.noContent().build();
    }

    // --- HÀM NGHIỆP VỤ: HOÀN TẤT CHUYẾN ---
    // Nhận HoanTatChuyenDiRequestDTO, Trả về ChuyenDiDTO (đã cập nhật)
    @PutMapping("/hoan-tat/{maChuyen}")
    public ResponseEntity<ChuyenDiDTO> hoanTatChuyen(
            @PathVariable String maChuyen,
            @RequestBody HoanTatChuyenDiRequestDTO dto) {

        ChuyenDiDTO chuyenDiDaCapNhat = chuyenDiService.hoanTatChuyenDi(maChuyen, dto.getSoKm());
        return ResponseEntity.ok(chuyenDiDaCapNhat);
    }

    // --- HÀM THỐNG KÊ (Giữ nguyên, vì đã là DTO) ---
    @GetMapping("/thong-ke-chuyen-theo-gio")
    public ResponseEntity<List<ThongKeChuyenTheoGio>> layThongKe() {
        List<ThongKeChuyenTheoGio> thongKe = chuyenDiService.layThongKeChuyenTheoGio();
        return ResponseEntity.ok(thongKe);
    }
}