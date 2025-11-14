package com.project.backend.repository;

import com.project.backend.model.LoaiXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiXeRepository extends JpaRepository<LoaiXe, String> {
    boolean existsByTenLoai(String tenLoai);
    boolean existsByMaLoai(String maLoai);
}