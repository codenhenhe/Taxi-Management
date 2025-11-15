package com.project.backend.repository;

// ... (imports)
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;


import com.project.backend.dto.XeStatsDTO;
import com.project.backend.model.Xe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; 


public interface XeRepository extends JpaRepository<Xe, String>, JpaSpecificationExecutor<Xe> {
    boolean existsByBienSoXe(String bienSoXe);
    boolean existsByMaXe(String maXe);

    // Sửa hàm findAll()
    @Query("SELECT x FROM Xe x LEFT JOIN FETCH x.loaiXe")
    List<Xe> findAllWithLoaiXe();

    // Sửa hàm findById()
    @Query("SELECT x FROM Xe x LEFT JOIN FETCH x.loaiXe WHERE x.maXe = :id")
    Optional<Xe> findByIdWithLoaiXe(@Param("id") String id);

    // --- Cho E3: sp_xe_hoat_dong ---
    @Query(value = "CALL sp_xe_hoat_dong()", nativeQuery = true)
    List<XeStatsDTO> getXeStats();
}