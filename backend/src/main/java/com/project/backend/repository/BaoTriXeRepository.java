package com.project.backend.repository;

import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.model.BaoTriXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // <-- Import

@Repository
public interface BaoTriXeRepository extends JpaRepository<BaoTriXe, String> {

    // --- Hàm Stored Procedure (Giữ nguyên) ---
    @Query(value = "CALL sp_MONTHLY_VEHICLE_FEE(:p_nam)", nativeQuery = true)
    List<ThongKePhiBaoTriHangThang> getMonthlyMaintenanceCost(@Param("p_nam") int year);

    // --- Thêm 2 hàm JOIN FETCH để chống N+1 Query ---

    @Query("SELECT bt FROM BaoTriXe bt LEFT JOIN FETCH bt.xe")
    List<BaoTriXe> findAllWithXe();

    @Query("SELECT bt FROM BaoTriXe bt LEFT JOIN FETCH bt.xe WHERE bt.maBaoTri = :id")
    Optional<BaoTriXe> findByIdWithXe(@Param("id") String id);
}