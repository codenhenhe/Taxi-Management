package com.project.backend.dto;

import lombok.Data;

@Data
public class KhachHangDTO {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;

    // Tuyệt đối không thêm List<ChuyenDiDTO> ở đây
    // nếu không sẽ gây lỗi vòng lặp vô hạn
}