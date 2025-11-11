package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "xe")
public class Xe {
    @Id
    @Column(name = "ma_xe", length = 50)
    private String maXe;

    @Column(name = "bien_so_xe", length = 20)
    private String bienSoXe;

    @Column(name = "mau_xe", length = 30)
    private String mauXe;

    @Column(name = "nha_sx", length = 50)
    private String nhaSanXuat;

    @Enumerated(EnumType.STRING)
    private TrangThaiXe trangThaiXe;

    @ManyToOne
    @JoinColumn(name = "ma_tai_xe")
    private TaiXe taiXe;

    @ManyToOne
    @JoinColumn(name = "ma_loai")
    private LoaiXe loaiXe;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL)
    private List<PhanCongXe> lichSuSuDung;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaoTriXe> danhSachBaoTri;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;

    public enum TrangThaiXe {
        SAN_SANG, BAO_TRI, DANG_CHAY
    }
}
