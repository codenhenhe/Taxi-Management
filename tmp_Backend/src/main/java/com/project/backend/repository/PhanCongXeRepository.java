package com.project.backend.repository;

import com.project.backend.model.PhanCongXe;
import com.project.backend.model.PhanCongXeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhanCongXeRepository extends JpaRepository<PhanCongXe, PhanCongXeId> {

    /**
     * TÌM CA PHÂN CÔNG ĐANG CHẠY (chưa có thời gian kết thúc) CỦA MỘT TÀI XẾ
     * Đây là hàm cho chức năng "Kết thúc ca"
     *
     * Giả sử trường @Id trong class TaiXe của bạn tên là 'maTaiXe'
     * Nếu nó tên là 'id', hãy sửa 'pcx.taiXe.maTaiXe' thành 'pcx.taiXe.id'
     */
    @Query("SELECT pcx FROM PhanCongXe pcx " +
            "WHERE pcx.taiXe.maTaiXe = :maTaiXe " +
            "AND pcx.thoiGianKetThuc IS NULL")
    Optional<PhanCongXe> findActiveAssignmentByTaiXe(@Param("maTaiXe") String maTaiXe);
}