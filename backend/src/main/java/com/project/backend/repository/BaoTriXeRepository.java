package com.project.backend.repository;

import com.project.backend.model.BaoTriXe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaoTriXeRepository extends JpaRepository<BaoTriXe, String> {
}