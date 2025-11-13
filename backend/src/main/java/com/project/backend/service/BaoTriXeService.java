package com.project.backend.service;

import com.project.backend.dto.BaoTriXeDTO; // <-- Import
import com.project.backend.dto.BaoTriXeRequestDTO; // <-- Import
import com.project.backend.dto.ThongKePhiBaoTriHangThang;
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.BaoTriXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.BaoTriXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Import

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors; // <-- Import

@Service
public class BaoTriXeService {

    @Autowired
    private BaoTriXeRepository baoTriXeRepository;

    @Autowired
    private XeRepository xeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<BaoTriXeDTO> getAllBaoTriXe() {
        // 1. Dùng hàm JOIN FETCH
        List<BaoTriXe> danhSachEntity = baoTriXeRepository.findAllWithXe();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper
                .collect(Collectors.toList());
    }

    public BaoTriXeDTO getBaoTriXeById(String id) {
        // 1. Dùng hàm JOIN FETCH
        BaoTriXe entity = timBaoTriXeBangId(id, true); // true = dùng JOIN FETCH
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    @Transactional // Thêm Transactional vì nó kích hoạt Trigger
    public BaoTriXeDTO createBaoTriXe(BaoTriXeRequestDTO dto) { // <-- Sửa
        // 1. Tìm chiếc xe tương ứng
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + dto.getMaXe()));

        // 2. Chuyển DTO -> Entity
        BaoTriXe baoTriXeMoi = new BaoTriXe();
        baoTriXeMoi.setNgayBaoTri(dto.getNgayBaoTri());
        baoTriXeMoi.setLoaiBaoTri(dto.getLoaiBaoTri());
        baoTriXeMoi.setChiPhi(dto.getChiPhi());
        baoTriXeMoi.setMoTa(dto.getMoTa());
        baoTriXeMoi.setXe(xe); // Gán object

        // 3. Logic nghiệp vụ (Tự tạo mã ID)
        String newId = "BT-" + UUID.randomUUID().toString().substring(0, 8);
        baoTriXeMoi.setMaBaoTri(newId);

        // 4. Lưu (Trigger CSDL sẽ chạy ở đây)
        BaoTriXe baoTriXeDaLuu = baoTriXeRepository.save(baoTriXeMoi);

        // 5. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(baoTriXeDaLuu);
    }

    @Transactional
    public BaoTriXeDTO updateBaoTriXe(String id, BaoTriXeRequestDTO dto) { // <-- Sửa
        // 1. Tìm lịch sử bảo trì cũ
        BaoTriXe baoTriXeHienTai = timBaoTriXeBangId(id, false); // false = không cần JOIN FETCH

        // 2. Tìm Xe (phòng trường hợp cho phép đổi xe)
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + dto.getMaXe()));

        // 3. Cập nhật thông tin từ DTO
        baoTriXeHienTai.setNgayBaoTri(dto.getNgayBaoTri());
        baoTriXeHienTai.setLoaiBaoTri(dto.getLoaiBaoTri());
        baoTriXeHienTai.setChiPhi(dto.getChiPhi());
        baoTriXeHienTai.setMoTa(dto.getMoTa());
        baoTriXeHienTai.setXe(xe); // Cho phép cập nhật xe liên quan

        // 4. Lưu lại
        BaoTriXe baoTriXeDaCapNhat = baoTriXeRepository.save(baoTriXeHienTai);

        // 5. Chuyển Entity -> DTO
        return chuyenSangDTO(baoTriXeDaCapNhat);
    }

    public void deleteBaoTriXe(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        BaoTriXe bx = timBaoTriXeBangId(id, false);
        // 2. Nếu tìm thấy, thì xóa
        baoTriXeRepository.delete(bx);
    }

    // --- HÀM THỐNG KÊ (Giữ nguyên) ---
    public List<ThongKePhiBaoTriHangThang> layThongKeChiPhiBaoTri(int year) {
        return baoTriXeRepository.getMonthlyMaintenanceCost(year);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity BaoTriXe sang BaoTriXeDTO ("làm phẳng")
     */
    private BaoTriXeDTO chuyenSangDTO(BaoTriXe entity) {
        if (entity == null)
            return null;

        BaoTriXeDTO dto = new BaoTriXeDTO();

        // 1. Map các trường của BaoTriXe
        dto.setMaBaoTri(entity.getMaBaoTri());
        dto.setNgayBaoTri(entity.getNgayBaoTri());
        dto.setLoaiBaoTri(entity.getLoaiBaoTri());
        dto.setChiPhi(entity.getChiPhi());
        dto.setMoTa(entity.getMoTa());

        // 2. Map "làm phẳng" từ Xe
        if (entity.getXe() != null) {
            dto.setMaXe(entity.getXe().getMaXe());
            dto.setBienSoXe(entity.getXe().getBienSoXe());
        }

        return dto;
    }

    /**
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private BaoTriXe timBaoTriXeBangId(String id, boolean useJoinFetch) {
        Optional<BaoTriXe> optionalBx;
        if (useJoinFetch) {
            optionalBx = baoTriXeRepository.findByIdWithXe(id);
        } else {
            optionalBx = baoTriXeRepository.findById(id);
        }

        return optionalBx
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử bảo trì với ID: " + id));
    }
}