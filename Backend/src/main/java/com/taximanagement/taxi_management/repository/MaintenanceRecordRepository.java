package com.taximanagement.taxi_management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.MaintenanceRecord;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    // Tìm kiếm lịch sử bảo trì của một xe cụ thể
    List<MaintenanceRecord> findByVehicleId(Long vehicleId);
    
    // Tìm kiếm các bản ghi bảo trì trong một khoảng ngày
    List<MaintenanceRecord> findByMaintenanceDateBetween(LocalDate start, LocalDate end);
    
    // Tìm kiếm các bản ghi có chi phí trên một mức nhất định
    List<MaintenanceRecord> findByCostGreaterThan(Double cost);
}