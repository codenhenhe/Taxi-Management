package com.project.backend.dto;

import com.project.backend.model.TrangThaiXe;
import lombok.Data;
// Không import LoaiXe (Entity)

@Data
public class XeDTO {

    private String maXe;
    private String bienSoXe;
    private String mauXe;
    private Integer namSanXuat;
    private TrangThaiXe trangThaiXe;

    // SỬA LẠI DÒNG NÀY:
    private LoaiXeDTO loaiXe; // <-- Đổi từ LoaiXe sang LoaiXeDTO

}
