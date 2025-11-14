package com.taximanagement.taxi_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taximanagement.taxi_management.dto.TripRequest;
import com.taximanagement.taxi_management.model.Trip;
import com.taximanagement.taxi_management.service.TripService;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // Lấy tất cả các chuyến đi
    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.findAll();
    }

    // Lấy chuyến đi theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Trip trip = tripService.findById(id);
        if (trip != null) {
            return ResponseEntity.ok(trip);
        }
        return ResponseEntity.notFound().build();
    }

    // Tạo chuyến đi mới (Booking)
    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody TripRequest tripRequest) {
        Trip newTrip = tripService.createTrip(tripRequest); // ✅ sửa đúng biến
        return ResponseEntity.ok(newTrip);
    }

    // Cập nhật trạng thái chuyến đi (Ví dụ: ACCEPTED, PICKUP, CANCELLED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable Long id, @RequestParam String status) {
        Trip updatedTrip = tripService.updateStatus(id, status);
        if (updatedTrip != null) {
            return ResponseEntity.ok(updatedTrip);
        }
        return ResponseEntity.notFound().build();
    }

    // Kết thúc chuyến đi và ghi nhận doanh thu
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Trip> completeTrip(@PathVariable Long id, @RequestParam Double finalFare) {
        Trip completedTrip = tripService.completeTrip(id, finalFare);
        if (completedTrip != null) {
            return ResponseEntity.ok(completedTrip);
        }
        return ResponseEntity.notFound().build();
    }
}