package com.project.backend.repository;

import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.model.ChuyenDi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional; // <-- Import

@Repository
public interface ChuyenDiRepository extends JpaRepository<ChuyenDi, String> {

    // --- Giữ nguyên các hàm Stored Procedure của bạn ---

    @Modifying
    @Transactional
    @Query(value = "CALL SP_HoanTatChuyenDi(:maChuyen, :soKm)", nativeQuery = true)
    void hoanTatChuyenDi(@Param("maChuyen") String maChuyen, @Param("soKm") Double soKm);

    @Query(value = "CALL sp_ThongKeChuyenTheoGio()", nativeQuery = true)
    List<ThongKeChuyenTheoGio> thongKeChuyenTheoGio();

    // --- Thêm 2 hàm JOIN FETCH để chống N+1 Query ---

    @Query("SELECT cd FROM ChuyenDi cd LEFT JOIN FETCH cd.xe x LEFT JOIN FETCH cd.khachHang kh")
    List<ChuyenDi> findAllWithDetails();

    @Query("SELECT cd FROM ChuyenDi cd LEFT JOIN FETCH cd.xe x LEFT JOIN FETCH cd.khachHang kh WHERE cd.maChuyen = :id")
    Optional<ChuyenDi> findByIdWithDetails(@Param("id") String id);
}