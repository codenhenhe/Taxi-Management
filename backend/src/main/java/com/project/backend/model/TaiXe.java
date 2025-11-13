package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Date;
import java.util.List;

@Entity
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maTaiXe") // An toàn hơn
@ToString(exclude = "lichSuPhanCong") // Ngắt vòng lặp khi log
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

    @Column(name = "sdt")
    private String soDienThoai;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTaiXe trangThai;

    // Thêm FetchType.LAZY để tối ưu
    @OneToMany(mappedBy = "taiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhanCongXe> lichSuPhanCong;
}