package com.project.backend.repository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.KhachHang;

import org.springframework.data.jpa.repository.JpaRepository;

// Interface này tự động kế thừa các hàm CRUD cơ bản
@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    // Không cần viết code thực thi ở đây
}
