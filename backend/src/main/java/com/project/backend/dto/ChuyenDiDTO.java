package com.project.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChuyenDiDTO {

    // --- Thông tin từ chính ChuyenDi ---
    private String maChuyen;
    private String diemDon;
    private String diemTra;
    private LocalDateTime tgDon;
    private LocalDateTime tgTra;
    private Double soKmDi;
    private Double cuocPhi;

    // --- Thông tin "làm phẳng" từ Xe ---
    private String maXe;
    private String bienSoXe; // Rất hữu ích cho frontend

    // --- Thông tin "làm phẳng" từ KhachHang ---
    private String maKhachHang;
    private String tenKhachHang; // Rất hữu ích cho frontend
    private String sdtKhachHang; // (Tên biến tự đặt)
}