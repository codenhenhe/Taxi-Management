package com.project.backend.repository;

// ... (imports)
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import com.project.backend.model.Xe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XeRepository extends JpaRepository<Xe, String> {
    boolean existsByBienSoXe(String bienSoXe);
    boolean existsByMaXe(String maXe);

    // Sửa hàm findAll()
    @Query("SELECT x FROM Xe x LEFT JOIN FETCH x.loaiXe")
    List<Xe> findAllWithLoaiXe();

    // Sửa hàm findById()
    @Query("SELECT x FROM Xe x LEFT JOIN FETCH x.loaiXe WHERE x.maXe = :id")
    Optional<Xe> findByIdWithLoaiXe(@Param("id") String id);
}