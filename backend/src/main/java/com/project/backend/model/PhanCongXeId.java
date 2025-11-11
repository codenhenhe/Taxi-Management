package com.project.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Quan trọng: Bắt buộc phải có cho khóa phức hợp
public class PhanCongXeId implements Serializable {

    // Không cần @Column, vì sẽ được map bằng @MapsId bên PhanCongXe
    // Kiểu dữ liệu (String) này phải khớp với @Id của TaiXe
    private String maTaiXe;

    // Không cần @Column, vì sẽ được map bằng @MapsId bên PhanCongXe
    // Kiểu dữ liệu (String) này phải khớp với @Id của Xe
    private String maXe;

    // Cần @Column vì đây là một phần của key, nhưng không phải là một quan hệ
    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;
}