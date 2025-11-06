package com.taximanagement.taxi_management.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity Bản ghi Bảo trì Xe
@Entity
@Table(name = "maintenance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    private LocalDate maintenanceDate; // Ngày bảo trì
    
    private String description; // Mô tả công việc đã làm
    
    private Double cost; // Chi phí bảo trì
    
    // Ghi lại số km của xe tại thời điểm bảo trì
    private Double mileageAtMaintenance; 

    // Người thực hiện/Đơn vị bảo trì
    private String serviceProvider; 
}