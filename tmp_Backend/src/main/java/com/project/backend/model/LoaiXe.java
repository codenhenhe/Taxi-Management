package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "loai_xe")
public class LoaiXe {
    @Id
    @Column(name = "ma_loai", length = 50)
    private String maLoai;

    @Column(name = "ten_loai", length = 50)
    private String tenLoai;

    @OneToMany(mappedBy = "loaiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Xe> danhSachXe;

    @OneToMany(mappedBy = "loaiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BangGia> danhSachBangGia;
}