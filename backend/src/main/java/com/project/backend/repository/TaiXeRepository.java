package com.project.backend.repository;

import com.project.backend.dto.RevenueByDriver;
import com.project.backend.dto.TaiXeStatsDTO;
import com.project.backend.model.TaiXe;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaiXeRepository extends JpaRepository<TaiXe, String> {
    
    @Query(value = "CALL sp_revenue_by_driver(:p_date)", nativeQuery = true)
    List<RevenueByDriver> getRevenueByDriver(@Param("p_date") LocalDate date);
    // --- Cho E2: sp_tong_tai_xe_hoat_dong ---
    @Query(value = "CALL sp_tong_tai_xe_hoat_dong()", nativeQuery = true)
    List<TaiXeStatsDTO> getTaiXeStats(); // Dùng List<> để hứng kết quả
}