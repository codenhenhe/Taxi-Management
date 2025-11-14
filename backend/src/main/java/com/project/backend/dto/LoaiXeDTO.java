package com.project.backend.dto;

import lombok.Data;

@Data
public class LoaiXeDTO {
    private String maLoai;
    private String tenLoai;
    private Integer soGhe;

    // Không chứa List<Xe> để ngắt vòng lặp
}