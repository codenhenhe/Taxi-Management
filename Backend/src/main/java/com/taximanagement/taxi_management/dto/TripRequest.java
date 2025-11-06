package com.taximanagement.taxi_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO chứa thông tin cần thiết để tạo một chuyến đi mới
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripRequest {
    
    // ID của khách hàng đặt xe
    private Long customerId;
    
    // Địa điểm đón khách
    private String pickupLocation;
    
    // Địa điểm trả khách dự kiến
    private String dropoffLocation;
    
    // Có thể thêm thời gian đặt trước
    // private LocalDateTime bookingTime; 
}