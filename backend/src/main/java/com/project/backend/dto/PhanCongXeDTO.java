package com.project.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhanCongXeDTO {

    // --- Lấy từ ID phức hợp ---
    private String maTaiXe;
    private String maXe;
    private LocalDateTime thoiGianBatDau;

    // --- Lấy từ trường riêng ---
    private LocalDateTime thoiGianKetThuc;

    // --- Lấy từ "làm phẳng" TaiXe ---
    private String tenTaiXe;

    // --- Lấy từ "làm phẳng" Xe ---
    private String bienSoXe;
}