package com.taximanagement.taxi_management.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO cho báo cáo tổng hợp doanh thu theo thời gian
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueReport {
    
    // Ngày bắt đầu của báo cáo
    private LocalDate startDateTime;
    
    // Ngày kết thúc của báo cáo
    private LocalDate endDateTime;
    
    // Tổng số chuyến đi hoàn thành
    private Long totalTrips;
    
    // Tổng doanh thu (tiền cước)
    private Double totalFareRevenue;
    
    // Tổng chi phí bảo trì/nhiên liệu (nếu tính toán được)
    private Double totalMaintenanceCost;
    
    // Lợi nhuận ròng
    private Double netProfit;
}