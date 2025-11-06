package com.taximanagement.taxi_management.controller;

import com.taximanagement.taxi_management.model.Driver;
import com.taximanagement.taxi_management.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriversController {

    @Autowired
    private DriverService driverService;

    // Lấy tất cả tài xế
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.findAll();
    }

    // Lấy thông tin tài xế theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Long id) {
        Driver driver = driverService.findById(id);
        if (driver != null) {
            return ResponseEntity.ok(driver);
        }
        return ResponseEntity.notFound().build();
    }

    // Thêm tài xế mới
    @PostMapping
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        // Có thể thêm Validation ở đây
        Driver savedDriver = driverService.save(driver);
        return ResponseEntity.ok(savedDriver);
    }

    // Cập nhật thông tin tài xế
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driverDetails) {
        Driver updatedDriver = driverService.update(id, driverDetails);
        if (updatedDriver != null) {
            return ResponseEntity.ok(updatedDriver);
        }
        return ResponseEntity.notFound().build();
    }

    // Xóa tài xế
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cập nhật trạng thái làm việc (Ví dụ: AVAILABLE, ON_TRIP, OFFLINE)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Driver> updateDriverStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Driver updatedDriver = driverService.updateStatus(id, newStatus);
        if (updatedDriver != null) {
            return ResponseEntity.ok(updatedDriver);
        }
        return ResponseEntity.notFound().build();
    }
}