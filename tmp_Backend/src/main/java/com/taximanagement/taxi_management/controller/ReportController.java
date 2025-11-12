package com.taximanagement.taxi_management.controller;

import com.taximanagement.taxi_management.dto.PerformanceReport;
import com.taximanagement.taxi_management.dto.RevenueReport;
import com.taximanagement.taxi_management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Lấy báo cáo doanh thu theo khoảng thời gian
    @GetMapping("/revenue")
    public RevenueReport getRevenueReport(
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        return reportService.generateRevenueReport(startDate, endDate);
    }

    // Lấy báo cáo hiệu suất tài xế
    @GetMapping("/driver-performance")
    public List<PerformanceReport> getDriverPerformanceReport(
            @RequestParam(required = false) Long driverId,
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        return reportService.generateDriverPerformanceReport(driverId, startDate, endDate);
    }

    // Lấy báo cáo chi phí bảo trì
    @GetMapping("/maintenance-cost")
    public Object getMaintenanceCostReport(
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        return reportService.generateMaintenanceCostReport(startDate, endDate);
    }
}