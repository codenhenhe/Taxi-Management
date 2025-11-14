package com.project.backend.repository;

import com.project.backend.model.BangGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BangGiaRepository extends JpaRepository<BangGia, String> {
}