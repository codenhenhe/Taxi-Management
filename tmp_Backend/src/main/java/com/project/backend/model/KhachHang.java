package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "KHACH_HANG")
@Data
public class KhachHang {

    @Id
    @Column(name = "ma_khach_hang", length = 50)
    private String maKhachHang;

    @Column(name = "ten_khach_hang", length = 100, nullable = false)
    private String tenKhachHang;

    @Column(name = "KH_sdt", length = 20, nullable = false, unique = true)
    private String sdt;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY)
    private List<ChuyenDi> danhSachChuyenDi;
}