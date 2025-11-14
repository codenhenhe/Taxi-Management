package com.taximanagement.taxi_management.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taximanagement.taxi_management.dto.RevenueReport;

@Service
public class ExportService {

    @Autowired
    private ReportService reportService;

    /**
     * Nghiệp vụ: Xuất báo cáo doanh thu sang CSV
     * Trả về ByteArrayInputStream để Controller có thể gửi dưới dạng File
     */
    public ByteArrayInputStream exportRevenueToCsv(LocalDate startDate, LocalDate endDate) {
        RevenueReport report = reportService.generateRevenueReport(startDate, endDate);
        
        // Sử dụng ByteArrayOutputStream để tạo file trong bộ nhớ
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); 
             PrintWriter writer = new PrintWriter(out)) {
            
            // Header CSV
            writer.println("Report Field,Value");
            
            // Data
            writer.printf("Start Date,%s\n", report.getStartDateTime());
            writer.printf("End Date,%s\n", report.getEndDateTime());
            writer.printf("Total Trips,%d\n", report.getTotalTrips());
            writer.printf("Total Fare Revenue,%.2f\n", report.getTotalFareRevenue());
            writer.printf("Total Maintenance Cost,%.2f\n", report.getTotalMaintenanceCost());
            writer.printf("Net Profit,%.2f\n", report.getNetProfit());

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to export data to CSV: " + e.getMessage());
        }
    }

    /**
     * Nghiệp vụ: Xuất báo cáo hiệu suất tài xế sang Excel (Template)
     */
    public ByteArrayInputStream exportDriverPerformanceToExcel(LocalDate startDate, LocalDate endDate) {
        // Trong dự án thực tế, bạn sẽ dùng Apache POI hoặc JXL để tạo file Excel.
        // Ở đây chỉ là placeholder, bạn cần thay thế bằng logic tạo Excel thực tế.
        return new ByteArrayInputStream("Excel Content Placeholder".getBytes()); 
    }
}