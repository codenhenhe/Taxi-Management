package com.project.backend.dto;

import java.math.BigDecimal;

// DTO để hứng kết quả từ SP B8
public interface ThongKePhiBaoTriHangThang {
    Integer getThangBaoTri(); // Tên hàm phải khớp với tên cột: thang_bao_tri

    BigDecimal getTongChiPhi(); // Tên hàm phải khớp với tên cột: tong_chi_phi
    // Dùng BigDecimal cho tiền tệ là an toàn nhất
}