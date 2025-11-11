package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @Column(name = "ma_khach_hang", length = 50)
    private String maKhachHang;

    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;
}
