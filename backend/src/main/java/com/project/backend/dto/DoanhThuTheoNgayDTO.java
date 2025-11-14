package com.project.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DoanhThuTheoNgayDTO {
    LocalDate getDate();

    BigDecimal getValue(); // Phải khớp tên "value" trong SQL
}