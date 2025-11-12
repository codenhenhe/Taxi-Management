package com.taximanagement.taxi_management.controller;

import com.taximanagement.taxi_management.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    // Xuất báo cáo doanh thu ra CSV
    @GetMapping("/revenue/csv")
    public ResponseEntity<InputStreamResource> exportRevenueToCsv(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        // Giả định service trả về ByteArrayInputStream của file CSV
        ByteArrayInputStream bis = exportService.exportRevenueToCsv(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=revenue_report.csv");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(new InputStreamResource(bis));
    }

    // Xuất báo cáo hiệu suất tài xế ra Excel
    @GetMapping("/driver-performance/xlsx")
    public ResponseEntity<InputStreamResource> exportDriverPerformanceToExcel(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        ByteArrayInputStream bis = exportService.exportDriverPerformanceToExcel(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=driver_performance.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}