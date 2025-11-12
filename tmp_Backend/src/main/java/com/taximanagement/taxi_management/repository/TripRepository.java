package com.taximanagement.taxi_management.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.Trip;
import com.taximanagement.taxi_management.model.Trip.TripStatus;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Tìm kiếm các chuyến đi theo trạng thái
    List<Trip> findByStatus(TripStatus status);
    
    // Tìm kiếm các chuyến đi của một tài xế cụ thể
    List<Trip> findByDriverId(Long driverId);
    
    // Tìm kiếm các chuyến đi trong khoảng thời gian (dùng cho báo cáo)
    List<Trip> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Lấy tất cả các chuyến đi đã hoàn thành trong một khoảng thời gian
    List<Trip> findByStatusAndEndTimeBetween(TripStatus status, LocalDateTime start, LocalDateTime end);
}