package com.project.backend.repository;

import com.project.backend.model.KhachHang; // Sửa thành KhachHang
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
// Sửa <Customer, Long> thành <KhachHang, String>
public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    // Không cần viết code thực thi ở đây
}