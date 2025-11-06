package com.taximanagement.taxi_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.Driver;
import com.taximanagement.taxi_management.model.Driver.DriverStatus;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Tìm kiếm tài xế theo trạng thái làm việc (Ví dụ: AVAILABLE)
    List<Driver> findByStatus(DriverStatus status);
    
    // Tìm kiếm tài xế theo số bằng lái xe
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    // Tìm kiếm tài xế theo tên
    List<Driver> findByFullNameContaining(String fullName);
}