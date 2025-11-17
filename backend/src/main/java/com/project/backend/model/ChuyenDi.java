package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "CHUYEN_DI")
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maChuyen") // An toàn hơn
@ToString(exclude = { "xe", "khachHang" }) // Ngắt vòng lặp khi log
public class ChuyenDi {

    @Id
    @Column(name = "ma_chuyen", length = 50)
    private String maChuyen;

    @Column(name = "diem_don", length = 255)
    private String diemDon;

    @Column(name = "diem_tra", length = 255)
    private String diemTra;

    @Column(name = "tg_don")
    private LocalDateTime tgDon;

    @Column(name = "tg_tra", nullable = true)
    private LocalDateTime tgTra;

    @Column(name = "so_km_di", nullable = true)
    private Double soKmDi;

    @Column(name = "cuoc_phi", nullable = true)
    private Double cuocPhi;

    @ManyToOne(fetch = FetchType.LAZY) // Đảm bảo là LAZY
    @JoinColumn(name = "ma_xe")
    private Xe xe;

    @ManyToOne(fetch = FetchType.LAZY) // Đảm bảo là LAZY
    @JoinColumn(name = "ma_khach_hang")
    private KhachHang khachHang;
}