package com.project.backend.dto;


import lombok.Data;
import java.time.LocalDateTime;
@Data
public class KetThucPhanCongRequestDTO {
    private String maTaiXe;
    private String maXe;
    private LocalDateTime thoiGianBatDau;
}