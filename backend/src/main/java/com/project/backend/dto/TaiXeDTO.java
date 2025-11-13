package com.project.backend.dto;

import com.project.backend.model.TrangThaiTaiXe;

import lombok.Data;
import java.util.Date;

@Data
public class TaiXeDTO {
    private String maTaiXe;
    private String tenTaiXe;
    private String soHieuGPLX;
    private Date ngaySinh;
    private String soDienThoai;
    private TrangThaiTaiXe trangThai;

    // ĐẢM BẢO KHÔNG CÓ DÒNG NÀY Ở ĐÂY:
    // private List<PhanCongXeDTO> lichSuPhanCong; // <-- CẤM CÓ
}