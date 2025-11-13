package com.project.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhanCongXeRequestDTO {
    private String maXe;
    private String maTaiXe;

    // Trường này là tùy chọn (nullable)
    private LocalDateTime thoiGianBatDau;
}