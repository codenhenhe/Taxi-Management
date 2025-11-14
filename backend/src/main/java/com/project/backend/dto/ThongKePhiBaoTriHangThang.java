package com.project.backend.dto;

import java.math.BigDecimal;

// DTO này dùng để hứng kết quả từ SP E8 (sp_MONTHLY_VEHICLE_FEE)
public interface ThongKePhiBaoTriHangThang {

    // Phải khớp với 'thang_bao_tri'
    Integer getThang_bao_tri();

    // Phải khớp với 'tong_chi_phi'
    BigDecimal getTong_chi_phi();
}