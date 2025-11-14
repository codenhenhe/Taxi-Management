package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "BAO_TRI_XE")
@Data
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

    @Column(name = "mo_ta", length = 100)
    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_xe")
    private Xe xe;
}