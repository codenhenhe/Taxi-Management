package com.project.backend.repository;

import com.project.backend.dto.PhanBoLoaiXeDTO;
import com.project.backend.model.LoaiXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoaiXeRepository extends JpaRepository<LoaiXe, String> {
    @Query(value = "CALL sp_ThongKePhanBoLoaiXe()", nativeQuery = true)
    List<PhanBoLoaiXeDTO> getPhanBoLoaiXe();
}