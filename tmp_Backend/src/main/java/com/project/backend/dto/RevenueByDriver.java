package com.project.backend.dto;

import java.math.BigDecimal;

// DTO để hứng kết quả từ SP B6
public interface RevenueByDriver {
    String getMaTaiXe();

    String getTenTaiXe();

    BigDecimal getTongDoanhThu();
}