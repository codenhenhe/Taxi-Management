package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Check;


@Entity
@Table(name = "BANG_GIA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_banggia_ma_loai", columnNames = {"ma_loai"})
    })
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maBangGia") // An toàn hơn
@ToString(exclude = "loaiXe") // Ngắt vòng lặp khi log
@Check(constraints = "gia_khoi_diem >= 0 AND gia_theo_km > 0 AND phu_thu >= 0")
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

    @ManyToOne(fetch = FetchType.LAZY) // Đảm bảo là LAZY
    @JoinColumn(name = "ma_loai")
    private LoaiXe loaiXe;
}