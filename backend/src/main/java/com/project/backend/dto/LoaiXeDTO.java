package com.project.backend.dto;

import lombok.Data;

@Data
public class LoaiXeDTO {
    private String maLoai;
    private String tenLoai;
    // Không chứa List<Xe> để ngắt vòng lặp
}