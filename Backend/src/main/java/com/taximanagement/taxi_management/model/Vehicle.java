package com.taximanagement.taxi_management.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity Xe Taxi
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate; // Biển số xe
    
    private String model;
    private Integer year;
    private Double mileage; // Số km đã đi
    
    @Enumerated(EnumType.STRING)
    private VehicleStatus status; // Trạng thái: ACTIVE, IN_MAINTENANCE, RETIRED

    // Mối quan hệ 1-nhiều: Một xe có nhiều bản ghi bảo trì
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRecord> maintenanceRecords;

    // Mối quan hệ 1-nhiều: Một xe có thể thực hiện nhiều chuyến đi (theo lịch sử)
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;

    public enum VehicleStatus {
        ACTIVE, IN_MAINTENANCE, RETIRED
    }
}