package com.project.backend.repository;

import com.project.backend.model.BaoTriXe;
import com.project.backend.dto.ThongKePhiBaoTriHangThang; // <-- Import DTO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaoTriXeRepository extends JpaRepository<BaoTriXe, String> {

    // --- Hàm cho Service (getAllBaoTriXe) ---
    // (Dùng JOIN FETCH để lấy "xe" ngay lập tức, chống lỗi Lazy Load)
    @Query("SELECT bt FROM BaoTriXe bt LEFT JOIN FETCH bt.xe")
    List<BaoTriXe> findAllWithXe();

    // --- Hàm cho Service (getBaoTriXeById) ---
    @Query("SELECT bt FROM BaoTriXe bt LEFT JOIN FETCH bt.xe WHERE bt.maBaoTri = :id")
    Optional<BaoTriXe> findByIdWithXe(@Param("id") String id);

    // --- Hàm cho Thống kê (E8) ---
    // (Gọi Stored Procedure E8 và map kết quả vào DTO)
    @Query(value = "CALL sp_MONTHLY_VEHICLE_FEE(:p_nam)", nativeQuery = true)
    List<ThongKePhiBaoTriHangThang> getMonthlyMaintenanceCost(@Param("p_nam") int year);
}