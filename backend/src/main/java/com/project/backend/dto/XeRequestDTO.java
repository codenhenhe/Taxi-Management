package com.project.backend.dto;

import com.project.backend.model.TrangThaiXe;
import lombok.Data;

// Tên file BẮT BUỘC phải là XeRequestDTO.java
@Data
public class XeRequestDTO {
    private String bienSoXe;
    private String mauXe;
    private String namSanXuat;
    private TrangThaiXe trangThaiXe;
    private String maLoai;
}