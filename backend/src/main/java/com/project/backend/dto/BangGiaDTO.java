package com.project.backend.dto;

import lombok.Data;

@Data
public class BangGiaDTO {
    private String maBangGia;
    private Double giaKhoiDiem;
    private Double giaTheoKm;
    private Double phuThu;

    // Lồng object LoaiXeDTO (đã tạo ở bước trước)
    private LoaiXeDTO loaiXe;
}