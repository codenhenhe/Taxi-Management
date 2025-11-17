package com.project.backend.model;

import jakarta.persistence.*;
// Bỏ @Data
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Date;

@Entity
@Table(name = "BAO_TRI_XE")
@Getter // Dùng @Getter
@Setter // Dùng @Setter
@EqualsAndHashCode(of = "maBaoTri") // An toàn hơn
@ToString(exclude = "xe") // Ngắt vòng lặp khi log
public class BaoTriXe {

    @Id
    @Column(name = "ma_bao_tri", length = 50)
    private String maBaoTri;

    @Column(name = "ngay_bao_tri")
    @Temporal(TemporalType.DATE)
    private Date ngayBaoTri;

    @Column(name = "loai_bao_tri", length = 100)
    private String loaiBaoTri;

    @Column(name = "chi_phi")
    private Double chiPhi;

    @Column(name = "mo_ta", length = 100, nullable = true)
    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY) // Đảm bảo là LAZY
    @JoinColumn(name = "ma_xe")
    private Xe xe;
}