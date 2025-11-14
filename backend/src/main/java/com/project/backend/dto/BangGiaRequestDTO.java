package com.project.backend.dto;

import lombok.Data;

@Data
public class BangGiaRequestDTO {
    // Không cần maBangGia (vì sẽ tự tạo hoặc lấy từ URL)
    private Double giaKhoiDiem;
    private Double giaTheoKm;
    private Double phuThu;

    // Chỉ cần ID của loại xe
    private String maLoai;
}