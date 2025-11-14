package com.taximanagement.taxi_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// Entity Tài xế
@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String licenseNumber; // Số bằng lái xe

    @Column(unique = true)
    private String phone;
    
    private LocalDate hireDate; // Ngày được thuê
    private Double averageRating; // Đánh giá trung bình

    // Trạng thái làm việc: AVAILABLE, ON_TRIP, OFFLINE, MAINTENANCE
    @Enumerated(EnumType.STRING)
    private DriverStatus status; 

    // Mối quan hệ 1-nhiều: Một tài xế có thể thực hiện nhiều chuyến đi
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;

    // Mối quan hệ 1-1: Mỗi tài xế lái một xe tại một thời điểm
    @OneToOne
    @JoinColumn(name = "current_vehicle_id", referencedColumnName = "id")
    private Vehicle currentVehicle;

    public enum DriverStatus {
        AVAILABLE, ON_TRIP, OFFLINE, MAINTENANCE
    }
}