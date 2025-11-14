package com.project.backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BaoTriXeDTO {

    // --- Thông tin từ BaoTriXe ---
    private String maBaoTri;
    private Date ngayBaoTri;
    private String loaiBaoTri;
    private Double chiPhi;
    private String moTa;

    // --- Thông tin "làm phẳng" từ Xe ---
    private String maXe;
    private String bienSoXe; // Rất hữu ích cho frontend
}