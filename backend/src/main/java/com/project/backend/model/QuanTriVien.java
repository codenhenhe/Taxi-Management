package com.project.backend.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;

@Entity
@Getter // <-- THÊM DÒNG NÀY
@Setter // <-- THÊM DÒNG NÀY
public class QuanTriVien {
    @Id
    @Column(name = "ma_qtv", length = 50)
    private String maQuanTriVien;

    @Column(name = "ten_qtv", length = 50, nullable = false)
    private String tenQuanTriVien;

    @Column(name = "ten_dang_nhap", length = 100, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "sdt", unique = true, length = 10)
    private String soDienThoai;

    @Column(name = "mat_khau", length = 255, nullable = false)
    private String matKhau;

    
}
