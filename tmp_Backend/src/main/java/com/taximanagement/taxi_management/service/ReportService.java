package com.taximanagement.taxi_management.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taximanagement.taxi_management.dto.PerformanceReport;
import com.taximanagement.taxi_management.dto.RevenueReport;
import com.taximanagement.taxi_management.model.Trip;
import com.taximanagement.taxi_management.model.Trip.TripStatus;
import com.taximanagement.taxi_management.repository.TripRepository;

@Service
public class ReportService {

    @Autowired
    private TripRepository tripRepository;

    /**
     * Nghiệp vụ: Tạo báo cáo doanh thu trong một khoảng thời gian
     */
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        // Lấy đến cuối ngày cuối cùng
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1).minusSeconds(1);

        // Lấy tất cả các chuyến đi đã hoàn thành trong kỳ
        List<Trip> completedTrips = tripRepository.findByStatusAndEndTimeBetween(
                TripStatus.COMPLETED, startDateTime, endDateTime);

        RevenueReport report = new RevenueReport();
        report.setStartDateTime(startDate);
        report.setEndDateTime(endDate);
        report.setTotalTrips((long) completedTrips.size());

        // Tính tổng doanh thu
        double totalRevenue = completedTrips.stream()
                .mapToDouble(Trip::getFare)
                .sum();
        report.setTotalFareRevenue(totalRevenue);
        
        // Giả định chi phí bảo trì và lợi nhuận ròng (cần logic phức tạp hơn trong thực tế)
        report.setTotalMaintenanceCost(0.0); 
        report.setNetProfit(totalRevenue);

        return report;
    }

    /**
     * Nghiệp vụ: Tạo báo cáo hiệu suất tài xế (Template)
     */
    public List<PerformanceReport> generateDriverPerformanceReport(Long driverId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1).minusSeconds(1);
        
        // Logic truy vấn các chuyến đi của tài xế và tính toán rating, số chuyến
        return List.of(); 
    }
    
    /**
     * Báo cáo chi phí bảo trì (Template)
     */
    public Object generateMaintenanceCostReport(LocalDate startDate, LocalDate endDate) {
        // Logic truy vấn MaintenanceRecordRepository
        return null;
    }
}