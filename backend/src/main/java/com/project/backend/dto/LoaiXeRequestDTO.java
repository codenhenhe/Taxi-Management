package com.project.backend.dto;

import lombok.Data;

@Data
public class LoaiXeRequestDTO {
    // Chỉ cần tenLoai, vì maLoai sẽ được tự tạo
    private String tenLoai;
}