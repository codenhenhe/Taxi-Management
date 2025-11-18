package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;
import org.hibernate.annotations.Check;


@Entity
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maLoai") // An toàn hơn
@ToString(exclude = { "danhSachXe", "danhSachBangGia" }) // Rất quan trọng
@Table(name = "loai_xe")
public class LoaiXe {
    @Id
    @Column(name = "ma_loai", length = 50)
    private String maLoai;

    @Column(name = "ten_loai", length = 50)
    private String tenLoai;

    @Column(name = "so_ghe")
    @Check(constraints = "so_ghe > 0")
    private Integer soGhe;

    @OneToMany(mappedBy = "loaiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Xe> danhSachXe;

    @OneToMany(mappedBy = "loaiXe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BangGia> danhSachBangGia;
}