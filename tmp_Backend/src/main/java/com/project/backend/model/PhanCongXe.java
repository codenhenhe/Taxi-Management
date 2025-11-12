package com.project.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id") // Chỉ so sánh dựa trên trường 'id'
@ToString(exclude = { "taiXe", "xe" }) // Loại bỏ các trường LAZY khỏi toString
@Table(name = "PHAN_CONG_XE")
public class PhanCongXe {

    @EmbeddedId
    private PhanCongXeId id; // id này đã chứa (maTaiXe, maXe, thoiGianBatDau)

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maTaiXe") // Map tới trường maTaiXe bên trong 'id'
    @JoinColumn(name = "ma_tai_xe")
    @JsonIgnoreProperties("danhSachXe")
    private TaiXe taiXe;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maXe") // Map tới trường maXe bên trong 'id'
    @JoinColumn(name = "ma_xe")
    @JsonIgnoreProperties({ "taiXe", "lichSuBaoTri", "danhSachChuyenDi" })
    private Xe xe;

    // thoiGianBatDau đã được chuyển vào PhanCongXeId

    // Chỉ còn lại thoiGianKetThuc
    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    // Bắt buộc có constructor rỗng cho JPA
    public PhanCongXe() {
    }
}