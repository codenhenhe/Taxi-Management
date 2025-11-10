package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "LOAI_XE")
@Data
public class LoaiXe {

    @Id
    @Column(name = "ma_loai", length = 50)
    private String maLoai;

    @Column(name = "ten_loai", length = 50, nullable = false)
    private String tenLoai;

    @OneToMany(mappedBy = "loaiXe", fetch = FetchType.LAZY)
    private List<Xe> danhSachXe;

    @OneToMany(mappedBy = "loaiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BangGia> danhSachBangGia;
}