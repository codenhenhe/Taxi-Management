package com.project.backend.repository;

import com.project.backend.model.ChuyenDi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface ChuyenDiRepository extends JpaRepository<ChuyenDi, String> {

    /**
     * SỬA LẠI HÀM NÀY:
     * (Bạn đã quên @Modifying và @Query)
     */

    // THÊM DÒNG NÀY (Vì SP này có CẬP NHẬT dữ liệu)
    @Modifying

    // THÊM DÒNG NÀY (Để báo đây là câu SQL, không phải tên hàm)
    @Query(value = "CALL SP_HoanTatChuyenDi(:p_ma_chuyen, :p_so_km_di)", nativeQuery = true)
    void hoanTatChuyenDi(
            @Param("p_ma_chuyen") String p_ma_chuyen,
            @Param("p_so_km_di") Double p_so_km_di);
}