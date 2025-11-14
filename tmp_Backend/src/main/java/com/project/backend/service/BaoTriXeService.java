package com.project.backend.service;

import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.model.BaoTriXe;
import com.project.backend.model.Xe; // Cần import Xe
import com.project.backend.repository.BaoTriXeRepository;
import com.project.backend.repository.XeRepository; // Cần XeRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Thêm import này


// 1. Đánh dấu đây là một Class Service
@Service
public class BaoTriXeService {

    // 2. Tiêm (Inject) Repository
    @Autowired
    private BaoTriXeRepository baoTriXeRepository;

    @Autowired
    private XeRepository xeRepository; // Cần để tìm xe

    /**
     * Hàm 1: Lấy tất cả lịch sử bảo trì
     * 
     * @return Danh sách tất cả lịch sử bảo trì
     */
    public List<BaoTriXe> getAllBaoTriXe() {
        // Chỉ cần gọi hàm findAll() của Repository
        return baoTriXeRepository.findAll();
    }

    /**
     * Hàm 2: Lấy một lịch sử bảo trì theo ID (Mã BT)
     * 
     * @param id Mã bảo trì (ví dụ: 'BT001')
     * @return Một đối tượng BaoTriXe
     */
    public BaoTriXe getBaoTriXeById(String id) {
        // findById trả về một Optional (có thể có hoặc không)
        Optional<BaoTriXe> bx = baoTriXeRepository.findById(id);

        // Nếu tìm thấy, trả về.
        // Nếu không, ném ra lỗi (Controller sẽ bắt lỗi này)
        return bx.orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử bảo trì với ID: " + id));
    }

    /**
     * Hàm 3: Tạo một lịch sử bảo trì mới (TỰ ĐỘNG TẠO MÃ)
     * 
     * @param baoTriXe Dữ liệu bảo trì mới từ Controller
     * @param maXe     Mã của chiếc xe được bảo trì
     * @return Lịch sử bảo trì đã được lưu
     */
    public BaoTriXe createBaoTriXe(BaoTriXe baoTriXe, String maXe) {
        // 1. Tìm chiếc xe tương ứng
        Xe xe = xeRepository.findById(maXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + maXe));

        // 2. Tự động tạo mã ID
        String newId = "BT-" + UUID.randomUUID().toString().substring(0, 8);
        baoTriXe.setMaBaoTri(newId);

        // 3. Gán chiếc xe này vào lịch sử bảo trì
        baoTriXe.setXe(xe);

        // 4. Lưu vào CSDL
        // LƯU Ý: Trigger `trg_update_verhicle_after_maintainment` [cite:
        // uploaded:schema.sql]
        // trong CSDL sẽ tự động chạy và cập nhật trạng thái xe thành 'Bảo trì'.
        return baoTriXeRepository.save(baoTriXe);
    }

    /**
     * Hàm 4: Cập nhật thông tin bảo trì
     * 
     * @param id              Mã bảo trì cần cập nhật
     * @param baoTriXeDetails Dữ liệu mới
     * @return Lịch sử bảo trì đã được cập nhật
     */
    public BaoTriXe updateBaoTriXe(String id, BaoTriXe baoTriXeDetails) {
        // 1. Tìm lịch sử bảo trì cũ
        BaoTriXe baoTriXeHienTai = getBaoTriXeById(id);

        // 2. Cập nhật thông tin (theo schema.sql) [cite: uploaded:schema.sql]
        baoTriXeHienTai.setNgayBaoTri(baoTriXeDetails.getNgayBaoTri());
        baoTriXeHienTai.setLoaiBaoTri(baoTriXeDetails.getLoaiBaoTri());
        baoTriXeHienTai.setChiPhi(baoTriXeDetails.getChiPhi());
        baoTriXeHienTai.setMoTa(baoTriXeDetails.getMoTa());
        // (Không cho phép cập nhật 'ma_xe')

        // 3. Lưu lại
        return baoTriXeRepository.save(baoTriXeHienTai);
    }

    /**
     * Hàm 5: Xóa một lịch sử bảo trì
     * 
     * @param id Mã bảo trì cần xóa
     */
    public void deleteBaoTriXe(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        BaoTriXe bx = getBaoTriXeById(id);

        // 2. Nếu tìm thấy, thì xóa
        baoTriXeRepository.delete(bx);
    }
    // Hàm 6: Bảo trì theo tháng
    public List<ThongKePhiBaoTriHangThang> layThongKeChiPhiBaoTri(int year) {
        return baoTriXeRepository.getMonthlyMaintenanceCost(year);
    }
}