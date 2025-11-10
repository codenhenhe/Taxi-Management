package com.project.backend.repository;

import com.project.backend.model.Xe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XeRepository extends JpaRepository<Xe, String> {
}