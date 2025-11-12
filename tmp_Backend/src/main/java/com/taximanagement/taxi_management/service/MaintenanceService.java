package com.taximanagement.taxi_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taximanagement.taxi_management.model.MaintenanceRecord;
import com.taximanagement.taxi_management.repository.MaintenanceRecordRepository;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    public List<MaintenanceRecord> findAll() {
        return maintenanceRecordRepository.findAll();
    }

    public MaintenanceRecord findById(Long id) {
        return maintenanceRecordRepository.findById(id).orElse(null);
    }

    public MaintenanceRecord save(MaintenanceRecord record) {
        return maintenanceRecordRepository.save(record);
    }

    public MaintenanceRecord update(Long id, MaintenanceRecord recordDetails) {
        MaintenanceRecord existingRecord = findById(id);
        if (existingRecord != null) {
            existingRecord.setMaintenanceDate(recordDetails.getMaintenanceDate());
            existingRecord.setDescription(recordDetails.getDescription());
            existingRecord.setCost(recordDetails.getCost());
            existingRecord.setMileageAtMaintenance(recordDetails.getMileageAtMaintenance());
            existingRecord.setServiceProvider(recordDetails.getServiceProvider());
            // Không cho phép thay đổi VehicleId
            return maintenanceRecordRepository.save(existingRecord);
        }
        return null;
    }
    
    public List<MaintenanceRecord> findByVehicleId(Long vehicleId) {
        return maintenanceRecordRepository.findByVehicleId(vehicleId);
    }

    public void delete(Long id) {
        maintenanceRecordRepository.deleteById(id);
    }
}