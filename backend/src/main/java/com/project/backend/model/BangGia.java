package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "BANG_GIA")
public class BangGia {
    @Id
    @Column(name = "ma_bang_gia", length = 50)
    private String maBangGia;

    @Column(name = "gia_khoi_diem", nullable = false)
    private Double giaKhoiDiem;
    
    @Column(name = "gia_theo_km", nullable = false)
    private Double giaTheoKm;

    @Column(name = "phu_thu")
    private Double phuThu;

    @ManyToOne
    @JoinColumn(name = "ma_loai")
    private LoaiXe loaiXe;
}
