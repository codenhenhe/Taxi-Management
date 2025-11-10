package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "XE")
@Data
public class Xe {

    @Id
    @Column(name = "ma_xe", length = 50)
    private String maXe;

    @Column(name = "bien_so_xe", length = 20, nullable = false, unique = true)
    private String bienSoXe;

    @Column(name = "mau_xe", length = 30)
    private String mauXe;

    @Column(name = "nha_sx", length = 50)
    private String nhaSx;

    @Column(name = "trang_thai_xe")
    @Enumerated(EnumType.STRING)
    private TrangThaiXe trangThaiXe; // Bộ chuyển đổi sẽ tự động áp dụng cho trường này

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tai_xe") // Tên cột FK trong bảng XE
    private TaiXe taiXe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai") // Tên cột FK trong bảng XE
    private LoaiXe loaiXe;

    @OneToMany(mappedBy = "xe", fetch = FetchType.LAZY)
    private List<BaoTriXe> lichSuBaoTri;

    @OneToMany(mappedBy = "xe", fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;
}