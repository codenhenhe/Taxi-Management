package com.project.backend.dto;

import java.math.BigDecimal; // Dùng BigDecimal cho an toàn

public interface SoSanhHomQuaDTO {
    Integer getSoHomNay();

    Integer getSoHomQua();

    BigDecimal getSoChuyenSoVoiHomQua(); // SQL trả về phép chia
}