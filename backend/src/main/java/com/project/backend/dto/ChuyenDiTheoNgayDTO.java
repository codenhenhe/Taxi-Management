package com.project.backend.dto;

import java.time.LocalDate;

public interface ChuyenDiTheoNgayDTO {
    LocalDate getDate();

    Integer getTrips(); // Phải khớp tên "trips" trong SQL
}