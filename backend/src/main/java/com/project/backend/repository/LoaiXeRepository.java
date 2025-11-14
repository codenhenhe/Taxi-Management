package com.project.backend.repository;

import com.project.backend.model.LoaiXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.dto.PhanBoLoaiXeDTO;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface LoaiXeRepository extends JpaRepository<LoaiXe, String> {
    boolean existsByTenLoai(String tenLoai);
    boolean existsByMaLoai(String maLoai);

    @Query(value = "CALL sp_ThongKePhanBoLoaiXe()", nativeQuery = true)
    List<PhanBoLoaiXeDTO> getPhanBoLoaiXe();
}