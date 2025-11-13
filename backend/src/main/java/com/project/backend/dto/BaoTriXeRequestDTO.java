package com.project.backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BaoTriXeRequestDTO {
    // Không cần maBaoTri (vì sẽ tự tạo hoặc lấy từ URL)
    private Date ngayBaoTri;
    private String loaiBaoTri;
    private Double chiPhi;
    private String moTa;

    // Thêm ID của xe liên quan
    private String maXe;
}