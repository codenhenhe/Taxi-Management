package com.taximanagement.taxi_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO tổng hợp chi tiết chuyến đi
public class TripDetailsDto {
    
    private Long tripId;
    private String customerName; // Từ Customer model
    private String driverName;   // Từ Driver model
    private String vehiclePlate; // Từ Vehicle model
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private BigDecimal fare;
    private String startLocation;
    private String endLocation;

    // Constructor, Getters và Setters (chỉ cung cấp ví dụ Getters)

    public Long getTripId() {
        return tripId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }
    
    // ... Các getters và setters khác
}