package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "CHUYEN_DI")
@Data
public class ChuyenDi {

    @Id
    @Column(name = "ma_chuyen", length = 50)
    private String maChuyen;

    @Column(name = "diem_don", length = 255)
    private String diemDon;

    @Column(name = "diem_tra", length = 255)
    private String diemTra;

    @Column(name = "tg_don")
    private LocalDateTime tgDon;

    @Column(name = "tg_tra")
    private LocalDateTime tgTra;

    @Column(name = "so_km_di")
    private Double soKmDi;

    @Column(name = "cuoc_phi")
    private Double cuocPhi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_xe")
    private Xe xe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khach_hang")
    private KhachHang khachHang;
}