package com.project.backend.dto;

import com.project.backend.model.TrangThaiTaiXe; // Import Enum
import lombok.Data;
import java.util.Date;

@Data
public class TaiXeRequestDTO {
    private String tenTaiXe;
    private String soHieuGPLX;
    private Date ngaySinh;
    private String soDienThoai;
    private TrangThaiTaiXe trangThai; // Cần cho hàm Update
}