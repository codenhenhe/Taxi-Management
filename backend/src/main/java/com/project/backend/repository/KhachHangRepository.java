package com.project.backend.repository;

import com.project.backend.model.KhachHang; // Sửa thành KhachHang
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; 


@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, String>, JpaSpecificationExecutor<KhachHang> {
    boolean existsBySdt(String sdt);
    boolean existsByMaKhachHang(String maKhachHang);

}