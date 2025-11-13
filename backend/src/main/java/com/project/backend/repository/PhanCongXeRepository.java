package com.project.backend.repository;

import com.project.backend.model.PhanCongXe;
import com.project.backend.model.PhanCongXeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- Import
import java.util.Optional;

@Repository
public interface PhanCongXeRepository extends JpaRepository<PhanCongXe, PhanCongXeId> {

    /**
     * TÌM CA PHÂN CÔNG ĐANG CHẠY (Giữ nguyên)
     */
    @Query("SELECT pcx FROM PhanCongXe pcx " +
            "WHERE pcx.taiXe.maTaiXe = :maTaiXe " +
            "AND pcx.thoiGianKetThuc IS NULL")
    Optional<PhanCongXe> findActiveAssignmentByTaiXe(@Param("maTaiXe") String maTaiXe);

    // --- Thêm 2 hàm JOIN FETCH để chống N+1 Query ---

    @Query("SELECT pcx FROM PhanCongXe pcx " +
            "LEFT JOIN FETCH pcx.xe x " +
            "LEFT JOIN FETCH pcx.taiXe t")
    List<PhanCongXe> findAllWithDetails();

    @Query("SELECT pcx FROM PhanCongXe pcx " +
            "LEFT JOIN FETCH pcx.xe x " +
            "LEFT JOIN FETCH pcx.taiXe t " +
            "WHERE pcx.id = :id")
    Optional<PhanCongXe> findByIdWithDetails(@Param("id") PhanCongXeId id);
}