package com.taximanagement.taxi_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taximanagement.taxi_management.model.Vehicle;
import com.taximanagement.taxi_management.repository.VehicleRepository;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(Long id, Vehicle vehicleDetails) {
        Vehicle existingVehicle = findById(id);
        if (existingVehicle != null) {
            existingVehicle.setLicensePlate(vehicleDetails.getLicensePlate());
            existingVehicle.setModel(vehicleDetails.getModel());
            existingVehicle.setYear(vehicleDetails.getYear());
            existingVehicle.setMileage(vehicleDetails.getMileage());
            existingVehicle.setStatus(vehicleDetails.getStatus());
            return vehicleRepository.save(existingVehicle);
        }
        return null;
    }

    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }
}