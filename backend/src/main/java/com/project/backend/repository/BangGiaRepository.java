package com.project.backend.repository;

import com.project.backend.model.BangGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Import
import org.springframework.data.repository.query.Param; // <-- Import

import java.util.List;
import java.util.Optional; // <-- Import

public interface BangGiaRepository extends JpaRepository<BangGia, String> {

    // Hàm này của bạn đã tốt, giữ lại!
    List<BangGia> findByLoaiXe_MaLoai(String maLoai);

    // --- Thêm 2 hàm JOIN FETCH để chống N+1 Query ---

    @Query("SELECT bg FROM BangGia bg LEFT JOIN FETCH bg.loaiXe")
    List<BangGia> findAllWithLoaiXe();

    @Query("SELECT bg FROM BangGia bg LEFT JOIN FETCH bg.loaiXe WHERE bg.maBangGia = :id")
    Optional<BangGia> findByIdWithLoaiXe(@Param("id") String id);
}