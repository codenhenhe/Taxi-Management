package com.project.backend.repository;

import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.model.BaoTriXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BaoTriXeRepository extends JpaRepository<BaoTriXe, String> {
    @Query(value = "CALL sp_MONTHLY_VEHICLE_FEE(:p_nam)", nativeQuery = true)
    List<ThongKePhiBaoTriHangThang> getMonthlyMaintenanceCost(@Param("p_nam") int year);
}