package com.taximanagement.taxi_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.Vehicle;
import com.taximanagement.taxi_management.model.Vehicle.VehicleStatus;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Tìm kiếm xe theo biển số xe (duy nhất)
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    // Tìm kiếm xe theo trạng thái (Ví dụ: IN_MAINTENANCE)
    List<Vehicle> findByStatus(VehicleStatus status);
    
    // Tìm kiếm xe theo mẫu xe và năm sản xuất
    List<Vehicle> findByModelAndYear(String model, Integer year);
}