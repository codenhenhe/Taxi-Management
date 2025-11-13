package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Entity
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maXe") // An toàn hơn
@ToString(exclude = { "lichSuSuDung", "danhSachBaoTri", "danhSachChuyenDi" }) // An toàn hơn
@Table(name = "xe")
public class Xe {

    // Không cần bất kỳ annotation JSON nào ở đây

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
    @Column(name = "trang_thai_xe")
    private TrangThaiXe trangThaiXe;

    @ManyToOne(fetch = FetchType.LAZY) // Thêm LAZY
    @JoinColumn(name = "ma_loai")
    private LoaiXe loaiXe;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhanCongXe> lichSuSuDung;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BaoTriXe> danhSachBaoTri;

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;

}