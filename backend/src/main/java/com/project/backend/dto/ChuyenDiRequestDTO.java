package com.project.backend.dto;

import lombok.Data;

@Data 
public class ChuyenDiRequestDTO {
    private String diemDon;
    private String diemTra;
    // ...

    // Chỉ cần ID
    private String maXe;
    private String maKhachHang;
}