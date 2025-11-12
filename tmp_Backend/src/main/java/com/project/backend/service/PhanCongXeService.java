package com.project.backend.service;

import com.project.backend.model.PhanCongXe;
import com.project.backend.model.PhanCongXeId;
import com.project.backend.model.TaiXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.PhanCongXeRepository;
import com.project.backend.repository.TaiXeRepository;
import com.project.backend.repository.XeRepository;
// Import Exception (Tạo 1 file mới cho class này)
import com.project.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PhanCongXeService {

    @Autowired
    private PhanCongXeRepository phanCongXeRepository;
    @Autowired
    private XeRepository xeRepository;
    @Autowired
    private TaiXeRepository taiXeRepository;

    public List<PhanCongXe> getAllPhanCongXe() {
        return phanCongXeRepository.findAll();
    }

    public PhanCongXe getPhanCongXeById(PhanCongXeId id) {
        return phanCongXeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phân công với ID: " + id));
    }

    @Transactional
    public PhanCongXe createPhanCongXe(String maXe, String maTaiXe, LocalDateTime thoiGianBatDau) {
        // 1. Tìm Xe và Tài xế, nếu không thấy thì báo lỗi 404
        Xe xe = xeRepository.findById(maXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + maXe));
        TaiXe taiXe = taiXeRepository.findById(maTaiXe)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài xế: " + maTaiXe));

        // 2. Xử lý thời gian bắt đầu
        // Nếu client không gửi (thoiGianBatDau == null), thì chúng ta mới tự động gán
        LocalDateTime thoiGianBatDauThucTe = (thoiGianBatDau != null) ? thoiGianBatDau : LocalDateTime.now();

        // 3. Tạo ID phức hợp
        PhanCongXeId newId = new PhanCongXeId(maTaiXe, maXe, thoiGianBatDauThucTe);

        // 4. Kiểm tra xem ca này đã tồn tại chưa
        if (phanCongXeRepository.existsById(newId)) {
            // Nên dùng 1 exception khác (ví dụ: 409 Conflict)
            throw new RuntimeException("Ca phân công này đã tồn tại.");
        }

        // 5. Tạo đối tượng Phân công mới và set quan hệ
        PhanCongXe phanCongMoi = new PhanCongXe();
        phanCongMoi.setId(newId);
        phanCongMoi.setXe(xe);
        phanCongMoi.setTaiXe(taiXe);
        // Mới tạo nên thoiGianKetThuc = null

        return phanCongXeRepository.save(phanCongMoi);
    }

    @Transactional
    public PhanCongXe ketThucPhanCong(String maTaiXe) {
        // 1. Dùng query custom đã viết trong Repository
        PhanCongXe phanCongHienTai = phanCongXeRepository.findActiveAssignmentByTaiXe(maTaiXe)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Tài xế '" + maTaiXe + "' không có ca nào đang chạy."));

        // 2. Cập nhật thời gian kết thúc
        phanCongHienTai.setThoiGianKetThuc(LocalDateTime.now());

        // 3. Lưu lại
        return phanCongXeRepository.save(phanCongHienTai);
    }

    public void deletePhanCongXe(PhanCongXeId id) {
        if (!phanCongXeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy phân công để xóa." + id);
        }
        phanCongXeRepository.deleteById(id);
    }
}