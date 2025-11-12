package com.taximanagement.taxi_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taximanagement.taxi_management.model.Driver;
import com.taximanagement.taxi_management.model.Driver.DriverStatus;
import com.taximanagement.taxi_management.repository.DriverRepository;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    public Driver findById(Long id) {
        return driverRepository.findById(id).orElse(null);
    }

    public Driver save(Driver driver) {
        // Thiết lập trạng thái mặc định khi tạo mới
        if (driver.getStatus() == null) {
            driver.setStatus(DriverStatus.OFFLINE); 
        }
        return driverRepository.save(driver);
    }

    public Driver update(Long id, Driver driverDetails) {
        Driver existingDriver = findById(id);
        if (existingDriver != null) {
            existingDriver.setFullName(driverDetails.getFullName());
            existingDriver.setLicenseNumber(driverDetails.getLicenseNumber());
            existingDriver.setPhone(driverDetails.getPhone());
            existingDriver.setAverageRating(driverDetails.getAverageRating());
            
            // Có thể bỏ qua cập nhật trạng thái nếu nó không được gửi trong payload update
            if (driverDetails.getStatus() != null) {
                existingDriver.setStatus(driverDetails.getStatus());
            }
            return driverRepository.save(existingDriver);
        }
        return null;
    }

    // Nghiệp vụ: Cập nhật trạng thái tài xế (thường gọi riêng)
    public Driver updateStatus(Long id, String newStatus) {
        Driver existingDriver = findById(id);
        if (existingDriver != null) {
            try {
                DriverStatus status = DriverStatus.valueOf(newStatus.toUpperCase());
                existingDriver.setStatus(status);
                return driverRepository.save(existingDriver);
            } catch (IllegalArgumentException e) {
                // Xử lý khi trạng thái không hợp lệ
                return null;
            }
        }
        return null;
    }

    public void delete(Long id) {
        driverRepository.deleteById(id);
    }
}