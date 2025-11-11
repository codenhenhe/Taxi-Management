package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "phan_cong_xe")
public class PhanCongXe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ma_tai_xe")
    private TaiXe taiXe;

    @ManyToOne
    @JoinColumn(name = "ma_xe")
    private Xe xe;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = true)
    private LocalDateTime thoiGianKetThuc;
}
