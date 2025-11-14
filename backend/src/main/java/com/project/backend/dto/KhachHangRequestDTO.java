package com.project.backend.dto;

import lombok.Data;

@Data
public class KhachHangRequestDTO {
    // Không cần maKhachHang (vì sẽ tự tạo hoặc lấy từ URL)
    private String tenKhachHang;
    private String sdt;
}