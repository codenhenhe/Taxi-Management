package com.project.backend.service;

import com.project.backend.model.LoaiXe;
import com.project.backend.model.TrangThaiXe; // Cần import Enum
import com.project.backend.model.Xe;
import com.project.backend.repository.LoaiXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class XeService {

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository; // Cần để gán loại xe

    /**
     * Hàm 1: Lấy tất cả xe
     */
    public List<Xe> getAllXe() {
        return xeRepository.findAll();
    }

    /**
     * Hàm 2: Lấy xe theo ID (Mã Xe)
     */
    public Xe getXeById(String id) {
        Optional<Xe> xe = xeRepository.findById(id);
        return xe.orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một xe mới (TỰ ĐỘNG TẠO MÃ)
     * 
     * @param xe     Dữ liệu xe (biển số, màu, nhà sx...)
     * @param maLoai Mã loại xe để gán
     * @return Xe đã được lưu
     */
    public Xe createXe(Xe xe, String maLoai) {

        LoaiXe loaiXe = loaiXeRepository.findById(maLoai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại xe: " + maLoai));

        // 2. Tự động tạo mã ID
        String newId = "XE-" + UUID.randomUUID().toString().substring(0, 8);
        xe.setMaXe(newId);

        // 3. Gán các đối tượng
        xe.setLoaiXe(loaiXe);

        // 4. Gán trạng thái mặc định (theo schema.sql DEFAULT 'Sẵn sàng')
        if (xe.getTrangThaiXe() == null) {
            xe.setTrangThaiXe(TrangThaiXe.SAN_SANG);
        }

        // 5. Lưu vào CSDL
        return xeRepository.save(xe);
    }

    /**
     * Hàm 4: Cập nhật thông tin xe
     * 
     * @param id        Mã xe cần cập nhật
     * @param xeDetails Dữ liệu mới
     * @return Xe đã được cập nhật
     */
    public Xe updateXe(String id, Xe xeDetails) {
        // 1. Tìm xe cũ
        Xe xeHienTai = getXeById(id);

        // 2. Cập nhật thông tin (theo schema.sql)
        xeHienTai.setBienSoXe(xeDetails.getBienSoXe());
        xeHienTai.setMauXe(xeDetails.getMauXe());
        xeHienTai.setNhaSanXuat(xeDetails.getNhaSanXuat());
        xeHienTai.setTrangThaiXe(xeDetails.getTrangThaiXe());

        // (Nếu muốn cập nhật tài xế hoặc loại xe,
        // bạn cần gửi maTaiXe/maLoai mới và tìm lại giống Hàm 3)

        // 3. Lưu lại
        return xeRepository.save(xeHienTai);
    }

    /**
     * Hàm 5: Xóa một xe
     * 
     * @param id Mã xe cần xóa
     */
    public void deleteXe(String id) {
        Xe xe = getXeById(id);
        xeRepository.delete(xe);
    }
}