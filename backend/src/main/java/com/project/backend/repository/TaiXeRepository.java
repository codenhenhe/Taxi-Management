package com.project.backend.repository;

import com.project.backend.dto.RevenueByDriver;
import com.project.backend.model.TaiXe;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaiXeRepository extends JpaRepository<TaiXe, String> {
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByMaTaiXe(String maTaiXe);
    @Query(value = "CALL sp_revenue_by_driver(:p_date)", nativeQuery = true)
    List<RevenueByDriver> getRevenueByDriver(@Param("p_date") LocalDate date);
}