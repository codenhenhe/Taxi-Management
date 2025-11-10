package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TAI_XE")
@Data
public class TaiXe {

    @Id
    @Column(name = "ma_tai_xe", length = 50)
    private String maTaiXe;

    @Column(name = "ten_tai_xe", length = 100, nullable = false)
    private String tenTaiXe;

    @Column(name = "so_hieu_GPLX", length = 50, nullable = false, unique = true)
    private String soHieuGPLX;

    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(length = 20)
    private String sdt;

    // THÊM LẠI DÒNG NÀY
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTaiXe trangThai;

    @OneToMany(mappedBy = "taiXe", fetch = FetchType.LAZY)
    private List<Xe> danhSachXe;
}