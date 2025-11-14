package com.project.backend.repository;

import com.project.backend.dto.ChuyenDiTheoNgayDTO;
import com.project.backend.dto.DoanhThuTheoNgayDTO;
import com.project.backend.dto.SoSanhHomQuaDTO;
import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.dto.TongKetNgayDTO;
import com.project.backend.dto.TongKetThangDTO;
import com.project.backend.dto.TopTaiXeDTO;
import com.project.backend.model.ChuyenDi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable; // <-- Thêm


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // <-- Import

@Repository
public interface ChuyenDiRepository extends JpaRepository<ChuyenDi, String> {
    // --- Thay thế cho D1: sp_chuyen_di ---
    @Query("SELECT COUNT(cd) FROM ChuyenDi cd WHERE FUNCTION('DATE', cd.tgDon) = :ngay")
    int countChuyenDiByNgay(@Param("ngay") LocalDate ngay);

    // --- Thay thế cho D2: sp_tinh_doanh_thu_theo_ngay ---
    @Query("SELECT COALESCE(SUM(cd.cuocPhi), 0) FROM ChuyenDi cd WHERE FUNCTION('DATE', cd.tgDon) = :ngay")
    BigDecimal sumDoanhThuByNgay(@Param("ngay") LocalDate ngay);

    // --- Thay thế cho D3: sp_tinh_doanh_thu_theo_thang ---
    @Query("SELECT COALESCE(SUM(cd.cuocPhi), 0) FROM ChuyenDi cd " +
            "WHERE FUNCTION('MONTH', cd.tgDon) = :thang AND FUNCTION('YEAR', cd.tgDon) = :nam")
    BigDecimal sumDoanhThuByThangNam(@Param("thang") int thang, @Param("nam") int nam);

    // --- Thay thế cho Hàm tính tổng chuyến đi theo tháng ---
    @Query("SELECT COUNT(cd) FROM ChuyenDi cd " +
            "WHERE FUNCTION('MONTH', cd.tgDon) = :thang AND FUNCTION('YEAR', cd.tgDon) = :nam")
    int countChuyenDiByThangNam(@Param("thang") int thang, @Param("nam") int nam);

    // --- Giữ nguyên các hàm Stored Procedure của bạn ---

    @Modifying
    @Transactional
    @Query(value = "CALL SP_HoanTatChuyenDi(:maChuyen, :soKm)", nativeQuery = true)
    void hoanTatChuyenDi(@Param("maChuyen") String maChuyen, @Param("soKm") Double soKm);

    @Query(value = "CALL sp_ThongKeChuyenTheoGio()", nativeQuery = true)
    List<ThongKeChuyenTheoGio> thongKeChuyenTheoGio();

    // --- Thêm 2 hàm JOIN FETCH để chống N+1 Query ---

    @Query("SELECT cd FROM ChuyenDi cd LEFT JOIN FETCH cd.xe x LEFT JOIN FETCH cd.khachHang kh")
    List<ChuyenDi> findAllWithDetails();

    @Query("SELECT cd FROM ChuyenDi cd LEFT JOIN FETCH cd.xe x LEFT JOIN FETCH cd.khachHang kh WHERE cd.maChuyen = :id")
    Optional<ChuyenDi> findByIdWithDetails(@Param("id") String id);
    // --- Cho E1: sp_so_voi_hom_qua ---
    @Query(value = "CALL sp_so_voi_hom_qua()", nativeQuery = true)
    List<SoSanhHomQuaDTO> getSoSanhHomQua();

    // --- Cho E4: sp_chuyen_di_gan_day ---
    // Chúng ta không cần SP cho cái này. Spring Data JPA làm tốt hơn.
    // Hàm này có nghĩa là: "Tìm tất cả ChuyenDi, sắp xếp theo tgDon Giảm dần"
    // Khi gọi từ Service, ta truyền Pageable(0, so_chuyen) vào là xong.
    List<ChuyenDi> findAllByOrderByTgDonDesc(Pageable pageable);

    // --- Cho E5: sp_tinh_doanh_thu_hom_nay ---
    @Query(value = "CALL sp_tinh_doanh_thu_hom_nay()", nativeQuery = true)
    BigDecimal getDoanhThuHomNay(); // SP này chỉ trả về 1 số

    // --- Cho E6: sp_tinh_doanh_thu_chuyen_di_theo_so_ngay ---
    @Query(value = "CALL sp_tinh_doanh_thu_chuyen_di_theo_so_ngay(:soNgay, :ngayTinh)", nativeQuery = true)
    List<TongKetNgayDTO> getTongKetTheoSoNgay(
            @Param("soNgay") int soNgay, 
            @Param("ngayTinh") LocalDate ngayTinh);

    // --- Cho E7: sp_tinh_doanh_thu_chuyen_di_theo_thang ---
    @Query(value = "CALL sp_tinh_doanh_thu_chuyen_di_theo_thang(:thangBD, :thangKT, :nam)", nativeQuery = true)
    List<TongKetThangDTO> getTongKetTheoThang(
            @Param("thangBD") int thangBD, 
            @Param("thangKT") int thangKT, 
            @Param("nam") int nam);

    @Query(value = "CALL sp_ThongKeDoanhThuTheoNgay(:soNgay)", nativeQuery = true)
    List<DoanhThuTheoNgayDTO> getDoanhThuTheoNgay(@Param("soNgay") int soNgay);

    @Query(value = "CALL sp_ThongKeChuyenDiTheoNgay(:soNgay)", nativeQuery = true)
    List<ChuyenDiTheoNgayDTO> getChuyenDiTheoNgay(@Param("soNgay") int soNgay);
    
    @Query(value = "CALL sp_ThongKeTopTaiXe()", nativeQuery = true)
    List<TopTaiXeDTO> getTopTaiXe();

}