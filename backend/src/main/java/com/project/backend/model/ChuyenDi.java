package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "chuyen_di")
public class ChuyenDi {
    @Id
    @Column(name = "ma_chuyen_di", length = 50)
    private String maChuyenDi;

    @Column(name = "diem_don", length = 255)
    private String diemDon;

    @Column(name = "diem_tra", length = 255)
    private String diemTra;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tg_don")
    private Date thoiGianDon;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tg_tra")
    private Date thoiGianTra;

    private Double soKmDi;
    private Double cuocPhi;

    @ManyToOne
    @JoinColumn(name = "ma_xe")
    private Xe xe;

    @ManyToOne
    @JoinColumn(name = "ma_khach_hang")
    private KhachHang khachHang;
}
