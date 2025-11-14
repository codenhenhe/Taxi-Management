package com.taximanagement.taxi_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO cho báo cáo hiệu suất của từng tài xế
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceReport {
    
    // ID của tài xế
    private Long driverId;
    
    // Tên tài xế
    private String driverName;
    
    // Số chuyến đi hoàn thành trong kỳ
    private Long completedTrips;
    
    // Tổng doanh thu cá nhân
    private Double totalRevenue;
    
    // Tỷ lệ chấp nhận chuyến (Acceptance Rate)
    private Double acceptanceRate; // (0.0 đến 1.0)
    
    // Điểm đánh giá trung bình
    private Double averageRating;
}