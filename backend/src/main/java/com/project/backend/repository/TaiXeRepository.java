package com.project.backend.repository;

import com.project.backend.model.TaiXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaiXeRepository extends JpaRepository<TaiXe, String> {
}