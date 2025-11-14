package com.project.backend.controller;

import com.project.backend.dto.*;
import com.project.backend.service.ChuyenDiService;
import com.project.backend.service.LoaiXeService; // <-- Sửa
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.backend.service.TaiXeService;
import com.project.backend.service.XeService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/thong-ke") // <-- URL PHẢI LÀ /api/stats
public class StatsPageController {
    @Autowired
    private TaiXeService taiXeService;
    @Autowired
    private XeService xeService;
    @Autowired
    private ChuyenDiService chuyenDiService;
    @Autowired
    private LoaiXeService loaiXeService; // <-- Sửa

    // 1. API cho biểu đồ Doanh thu (LineChart)
    // React gọi: /api/stats/revenue?range=7days
    @GetMapping("/revenue")
    public ResponseEntity<List<DoanhThuTheoNgayDTO>> getRevenueStats(@RequestParam String range) {
        int soNgay = parseRange(range);
        return ResponseEntity.ok(chuyenDiService.getStatsRevenue(soNgay));
    }

    // 2. API cho biểu đồ Chuyến đi (BarChart)
    // React gọi: /api/stats/trips?range=7days
    @GetMapping("/trips")
    public ResponseEntity<List<ChuyenDiTheoNgayDTO>> getTripStats(@RequestParam String range) {
        int soNgay = parseRange(range);
        return ResponseEntity.ok(chuyenDiService.getStatsTrips(soNgay));
    }

    // 3. API cho biểu đồ Loại xe (PieChart)
    // React gọi: /api/stats/vehicle-types
    @GetMapping("/vehicle-types")
    public ResponseEntity<List<PhanBoLoaiXeDTO>> getVehicleTypeStats() {
        return ResponseEntity.ok(loaiXeService.getStatsPhanBoLoaiXe());
    }

    // API E2 (cho Dashboard): /api/stats/tai-xe-stats
    @GetMapping("/tai-xe-stats")
    public ResponseEntity<List<TaiXeStatsDTO>> getTaiXeStats() {
        return ResponseEntity.ok(taiXeService.getTaiXeStats());
    }

    // API E3 (cho Dashboard): /api/stats/xe-stats
    @GetMapping("/xe-stats")
    public ResponseEntity<List<XeStatsDTO>> getXeStats() {
        return ResponseEntity.ok(xeService.getXeStats());
    }

    // API E1 (cho Dashboard): /api/stats/so-sanh-hom-qua
    @GetMapping("/so-sanh-hom-qua")
    public ResponseEntity<List<SoSanhHomQuaDTO>> getSoSanhHomQua() {
        return ResponseEntity.ok(chuyenDiService.getSoSanhHomQua());
    }

    // API E4 (cho Dashboard): /api/stats/chuyen-di-gan-day
    @GetMapping("/chuyen-di-gan-day")
    public ResponseEntity<List<ChuyenDiDTO>> getChuyenDiGanDay(
            @RequestParam(defaultValue = "5") int soChuyen) { // Dashboard chỉ cần 5
        return ResponseEntity.ok(chuyenDiService.getChuyenDiGanDay(soChuyen));
    }

    // API E5 (cho Dashboard): /api/stats/doanh-thu-hom-nay
    @GetMapping("/doanh-thu-hom-nay")
    public ResponseEntity<BigDecimal> getDoanhThuHomNay() {
        return ResponseEntity.ok(chuyenDiService.getDoanhThuHomNay());
    }

    // 4. API cho bảng Top Tài xế
    // React gọi: /api/stats/driver-performance
    @GetMapping("/driver-performance")
    public ResponseEntity<List<TopTaiXeDTO>> getDriverPerformanceStats() {
        return ResponseEntity.ok(chuyenDiService.getStatsTopTaiXe());
    }

    // --- Hàm Helper để đổi "7days" -> 7 ---
    private int parseRange(String range) {
        switch (range) {
            case "30days":
                return 30;
            case "month":
                return 30; // Giả sử tháng này = 30 ngày
            case "year":
                return 365;
            case "7days":
            default:
                return 7;
        }
    }
}