package com.project.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable // Đánh dấu là 1 phần của Entity khác
@Getter
@Setter
@NoArgsConstructor // Bắt buộc
@AllArgsConstructor // Bắt buộc
@EqualsAndHashCode // Bắt buộc
public class PhanCongXeId implements Serializable {

    @Column(name = "ma_tai_xe")
    private String maTaiXe;

    @Column(name = "ma_xe")
    private String maXe;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;
}