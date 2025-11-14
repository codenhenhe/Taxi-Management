package com.taximanagement.taxi_management.controller;

import com.taximanagement.taxi_management.model.MaintenanceRecord;
import com.taximanagement.taxi_management.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    // Lấy tất cả các bản ghi bảo trì
    @GetMapping
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        return maintenanceService.findAll();
    }

    // Lấy bản ghi bảo trì theo ID
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRecord> getMaintenanceRecordById(@PathVariable Long id) {
        MaintenanceRecord record = maintenanceService.findById(id);
        if (record != null) {
            return ResponseEntity.ok(record);
        }
        return ResponseEntity.notFound().build();
    }

    // Tạo bản ghi bảo trì mới
    @PostMapping
    public ResponseEntity<MaintenanceRecord> createMaintenanceRecord(@RequestBody MaintenanceRecord record) {
        MaintenanceRecord savedRecord = maintenanceService.save(record);
        return ResponseEntity.ok(savedRecord);
    }

    // Cập nhật bản ghi bảo trì
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRecord> updateMaintenanceRecord(@PathVariable Long id, @RequestBody MaintenanceRecord recordDetails) {
        MaintenanceRecord updatedRecord = maintenanceService.update(id, recordDetails);
        if (updatedRecord != null) {
            return ResponseEntity.ok(updatedRecord);
        }
        return ResponseEntity.notFound().build();
    }

    // Lấy lịch sử bảo trì của một xe cụ thể
    @GetMapping("/vehicle/{vehicleId}")
    public List<MaintenanceRecord> getMaintenanceByVehicle(@PathVariable Long vehicleId) {
        return maintenanceService.findByVehicleId(vehicleId);
    }
}