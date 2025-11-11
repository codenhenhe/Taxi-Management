package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "bao_tri_xe")
public class BaoTriXe {
    @Id
    @Column(name = "ma_bao_tri", length = 50)
    private String maBaoTri;

    @Temporal(TemporalType.DATE)
    private Date ngayBaoTri;

    @Column(name = "loai_bao_tri", length = 50)
    private String loaiBaoTri;

    private Double chiPhi;

    @Column(length = 100)
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "ma_xe")
    private Xe xe;
}
