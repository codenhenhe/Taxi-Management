package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "tai_xe")
public class TaiXe {
    @Id
    @Column(name = "ma_tai_xe", length = 50)
    private String maTaiXe;

    @Column(name = "ten_tai_xe", length = 100, nullable = false)
    private String tenTaiXe;

    @Column(name = "so_hieu_GPLX", length = 50, nullable = false, unique = true)
    private String soHieuGPLX;

    @Column(name = "ngay_sinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

 
    // ...
    @Column(name = "sdt") // <-- Thêm dòng này để map với CSDL
    private String soDienThoai;
    // ...

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTaiXe trangThai;

    @OneToMany(mappedBy = "taiXe", cascade = CascadeType.ALL)
    private List<PhanCongXe> lichSuPhanCong;

    // THÊM LẠI DÒNG NÀY
    
}
    
  