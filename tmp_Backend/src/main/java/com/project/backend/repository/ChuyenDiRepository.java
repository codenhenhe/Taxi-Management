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

@Repository
public interface ChuyenDiRepository extends JpaRepository<ChuyenDi, String> {

    
    @Modifying

    @Transactional
    @Query(value = "CALL SP_HoanTatChuyenDi(:maChuyen, :soKm)", nativeQuery = true)
    void hoanTatChuyenDi(@Param("maChuyen") String maChuyen, @Param("soKm") Double soKm);

    @Query(value = "CALL sp_ThongKeChuyenTheoGio()", nativeQuery = true)
    List<ThongKeChuyenTheoGio> thongKeChuyenTheoGio();
        }