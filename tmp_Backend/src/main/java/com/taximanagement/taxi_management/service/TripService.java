package com.taximanagement.taxi_management.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taximanagement.taxi_management.dto.TripRequest;
import com.taximanagement.taxi_management.model.Customer;
import com.taximanagement.taxi_management.model.Driver;
import com.taximanagement.taxi_management.model.Trip; // Import Transactional
import com.taximanagement.taxi_management.model.Trip.TripStatus;
import com.taximanagement.taxi_management.repository.CustomerRepository;
import com.taximanagement.taxi_management.repository.DriverRepository;
import com.taximanagement.taxi_management.repository.TripRepository;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public Trip findById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    /**
     * Nghiệp vụ: Tạo chuyến đi mới (Booking)
     * Bao gồm logic tìm tài xế và cập nhật trạng thái
     */
    @Transactional // Đảm bảo việc lưu Trip và Driver là Atomic
    public Trip createTrip(TripRequest tripRequest) {
        Customer customer = customerRepository.findById(tripRequest.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // 1. Tìm tài xế khả dụng gần nhất (Giả định tìm thấy tài xế AVAILABLE đầu tiên)
        Driver availableDriver = driverRepository.findByStatus(Driver.DriverStatus.AVAILABLE)
                .stream().findFirst().orElse(null); 
        
        Trip trip = new Trip();
        trip.setCustomer(customer);
        trip.setPickupLocation(tripRequest.getPickupLocation());
        trip.setDropoffLocation(tripRequest.getDropoffLocation());
        trip.setStartTime(LocalDateTime.now());
        
        if (availableDriver != null) {
            trip.setDriver(availableDriver);
            // Gán trạng thái đã được chấp nhận (ACCEPTED)
            trip.setStatus(TripStatus.ACCEPTED);
            
            // Cập nhật trạng thái tài xế sang Đang đi chuyến
            availableDriver.setStatus(Driver.DriverStatus.ON_TRIP);
            driverRepository.save(availableDriver);
            
            // Giả định gán xe từ tài xế hiện tại
            trip.setVehicle(availableDriver.getCurrentVehicle());
        } else {
            // Không tìm thấy tài xế, chuyến đi đang chờ (PENDING)
            trip.setStatus(TripStatus.PENDING);
        }

        return tripRepository.save(trip);
    }
    
    /**
     * Nghiệp vụ: Hoàn thành chuyến đi và tính toán cước phí
     */
    @Transactional
    public Trip completeTrip(Long id, Double finalFare) {
        Trip trip = findById(id);
        if (trip != null && trip.getStatus() == TripStatus.IN_PROGRESS) {
            
            // 1. Cập nhật thông tin chuyến đi
            trip.setStatus(TripStatus.COMPLETED);
            trip.setEndTime(LocalDateTime.now());
            trip.setFare(finalFare);
            
            // 2. Cập nhật trạng thái tài xế về AVAILABLE
            if (trip.getDriver() != null) {
                Driver driver = trip.getDriver();
                driver.setStatus(Driver.DriverStatus.AVAILABLE);
                driverRepository.save(driver);
            }
            
            return tripRepository.save(trip);
        }
        return null;
    }
    
    // Cập nhật trạng thái
    public Trip updateStatus(Long id, String status) {
        Trip trip = findById(id);
        if (trip != null) {
             try {
                trip.setStatus(TripStatus.valueOf(status.toUpperCase()));
                return tripRepository.save(trip);
            } catch (IllegalArgumentException e) {
                // Trạng thái không hợp lệ
                return null;
            }
        }
        return null;
    }
}