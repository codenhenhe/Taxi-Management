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

    @Column(name = "nam_sx", length = 50)
    private String namSanXuat;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_xe")
    private TrangThaiXe trangThaiXe;

 
    @ManyToOne
    @JoinColumn(name = "ma_loai")
    private LoaiXe loaiXe;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL)
    private List<PhanCongXe> lichSuSuDung;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaoTriXe> danhSachBaoTri;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;

}