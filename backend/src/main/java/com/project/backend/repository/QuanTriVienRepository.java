package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.QuanTriVien;

@Repository public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, String> {

}
