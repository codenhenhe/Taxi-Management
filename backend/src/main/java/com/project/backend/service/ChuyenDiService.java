package com.project.backend.service;

import com.project.backend.dto.ChuyenDiDTO; // <-- Import
import com.project.backend.dto.ChuyenDiRequestDTO; // <-- Import
import com.project.backend.dto.ChuyenDiTheoNgayDTO;
import com.project.backend.dto.DoanhThuTheoNgayDTO;
import com.project.backend.dto.TopTaiXeDTO;
import com.project.backend.dto.SoSanhHomQuaDTO;
import com.project.backend.dto.ThongKeChuyenTheoGio;
import com.project.backend.dto.TongKetNgayDTO;
import com.project.backend.dto.TongKetThangDTO;
import com.project.backend.exception.ResourceNotFoundException; // (Nên dùng)
import com.project.backend.model.ChuyenDi;
import com.project.backend.model.KhachHang;
import com.project.backend.model.Xe;
import com.project.backend.repository.ChuyenDiRepository;
import com.project.backend.repository.KhachHangRepository;
import com.project.backend.repository.XeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors; // <-- Import

@Service
public class ChuyenDiService {

    @Autowired
    private ChuyenDiRepository chuyenDiRepository;

    @Autowired
    private XeRepository xeRepository; // Cần để tìm Xe

    @Autowired
    private KhachHangRepository khachHangRepository; // Cần để tìm KhachHang

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<ChuyenDiDTO> getAllChuyenDi() {
        // 1. Dùng hàm JOIN FETCH để chống N+1 Query
        List<ChuyenDi> danhSachEntity = chuyenDiRepository.findAllWithDetails();
        // 2. Chuyển List<Entity> -> List<DTO>
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO) // Dùng hàm helper
                .collect(Collectors.toList());
    }

    public ChuyenDiDTO getChuyenDiById(String id) {
        // 1. Dùng hàm JOIN FETCH
        ChuyenDi entity = timChuyenDiBangId(id, true); // true = dùng JOIN FETCH
        // 2. Chuyển Entity -> DTO
        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    @Transactional // Thêm Transactional cho an toàn
    public ChuyenDiDTO createChuyenDi(ChuyenDiRequestDTO dto) {
        // 1. Tìm Xe và Khách Hàng
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + dto.getMaXe()));
        KhachHang kh = khachHangRepository.findById(dto.getMaKhachHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng: " + dto.getMaKhachHang()));

        // 2. Chuyển DTO -> Entity
        ChuyenDi chuyenDiMoi = new ChuyenDi();
        chuyenDiMoi.setDiemDon(dto.getDiemDon());
        chuyenDiMoi.setDiemTra(dto.getDiemTra());

        // 3. Gán các đối tượng liên quan
        chuyenDiMoi.setXe(xe);
        chuyenDiMoi.setKhachHang(kh);

        // 4. Logic nghiệp vụ (Giữ lại logic của bạn)
        String newId = "CD-" + UUID.randomUUID().toString().substring(0, 8);
        chuyenDiMoi.setMaChuyen(newId);
        chuyenDiMoi.setTgDon(LocalDateTime.now());

        // 5. Lưu Entity (Sẽ kích hoạt Trigger)
        ChuyenDi chuyenDiDaLuu = chuyenDiRepository.save(chuyenDiMoi);

        // 6. Chuyển Entity đã lưu -> DTO để trả về
        return chuyenSangDTO(chuyenDiDaLuu);
    }

    @Transactional
    public ChuyenDiDTO updateChuyenDi(String id, ChuyenDiRequestDTO dto) {
        // 1. Tìm chuyến đi cũ
        ChuyenDi chuyenDiHienTai = timChuyenDiBangId(id, false); // false = không cần JOIN FETCH

        // 2. Tìm các đối tượng liên quan (nếu có cập nhật)
        Xe xe = xeRepository.findById(dto.getMaXe())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe: " + dto.getMaXe()));
        KhachHang kh = khachHangRepository.findById(dto.getMaKhachHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng: " + dto.getMaKhachHang()));

        // 3. Cập nhật thông tin từ DTO
        chuyenDiHienTai.setDiemDon(dto.getDiemDon());
        chuyenDiHienTai.setDiemTra(dto.getDiemTra());
        chuyenDiHienTai.setXe(xe); // Cho phép cập nhật cả xe
        chuyenDiHienTai.setKhachHang(kh); // Cho phép cập nhật cả khách

        // 4. Lưu lại
        ChuyenDi chuyenDiDaCapNhat = chuyenDiRepository.save(chuyenDiHienTai);

        // 5. Chuyển Entity -> DTO
        return chuyenSangDTO(chuyenDiDaCapNhat);
    }

    public void deleteChuyenDi(String id) {
        // 1. Tìm (để chắc chắn nó tồn tại)
        ChuyenDi cd = timChuyenDiBangId(id, false);
        // 2. Nếu tìm thấy, thì xóa
        chuyenDiRepository.delete(cd);
    }

    // --- CÁC HÀM NGHIỆP VỤ ---

    @Transactional
    public ChuyenDiDTO hoanTatChuyenDi(String id, Double soKmDi) {
        // 1. Gọi Stored Procedure
        chuyenDiRepository.hoanTatChuyenDi(id, soKmDi);

        // 2. Lấy lại dữ liệu đã được SP cập nhật (DÙNG JOIN FETCH)
        ChuyenDi chuyenDiDaCapNhat = timChuyenDiBangId(id, true);

        // 3. Chuyển sang DTO để trả về
        return chuyenSangDTO(chuyenDiDaCapNhat);
    }

    public List<ThongKeChuyenTheoGio> layThongKeChuyenTheoGio() {
        return chuyenDiRepository.thongKeChuyenTheoGio();
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    /**
     * Hàm private để chuyển Entity ChuyenDi sang ChuyenDiDTO ("làm phẳng")
     */
    private ChuyenDiDTO chuyenSangDTO(ChuyenDi entity) {
        if (entity == null)
            return null;

        ChuyenDiDTO dto = new ChuyenDiDTO();

        // 1. Map các trường của ChuyenDi
        dto.setMaChuyen(entity.getMaChuyen());
        dto.setDiemDon(entity.getDiemDon());
        dto.setDiemTra(entity.getDiemTra());
        dto.setTgDon(entity.getTgDon());
        dto.setTgTra(entity.getTgTra());
        dto.setSoKmDi(entity.getSoKmDi());
        dto.setCuocPhi(entity.getCuocPhi());

        // 2. Map "làm phẳng" từ Xe
        if (entity.getXe() != null) {
            dto.setMaXe(entity.getXe().getMaXe());
            dto.setBienSoXe(entity.getXe().getBienSoXe());
        }

        // 3. Map "làm phẳng" từ KhachHang
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getTenKhachHang());
            dto.setSdtKhachHang(entity.getKhachHang().getSdt()); // Lấy sdt
        }

        return dto;
    }

    /**
     * Hàm private để tìm Entity (Tái sử dụng)
     */
    private ChuyenDi timChuyenDiBangId(String id, boolean useJoinFetch) {
        Optional<ChuyenDi> optionalCd;
        if (useJoinFetch) {
            optionalCd = chuyenDiRepository.findByIdWithDetails(id);
        } else {
            optionalCd = chuyenDiRepository.findById(id);
        }

        return optionalCd.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + id));
    }
    // E1: sp_so_voi_hom_qua
    @Transactional(readOnly = true)
    public List<SoSanhHomQuaDTO> getSoSanhHomQua() {
        return chuyenDiRepository.getSoSanhHomQua();
    }

    // E4: sp_chuyen_di_gan_day (Viết lại bằng JPA, trả về DTO)
    @Transactional(readOnly = true)
    public List<ChuyenDiDTO> getChuyenDiGanDay(int soChuyen) {
        // Lấy 0, soChuyen (ví dụ: trang 0, 10 phần tử)
        Pageable pageable = PageRequest.of(0, soChuyen); 
        
        List<ChuyenDi> entities = chuyenDiRepository.findAllByOrderByTgDonDesc(pageable);
        
        // Chuyển List<Entity> -> List<DTO> để tránh đệ quy
        return entities.stream()
                .map(this::chuyenSangDTO) // Dùng helper
                .collect(Collectors.toList());
    }

    // E5: sp_tinh_doanh_thu_hom_nay
    @Transactional(readOnly = true)
    public BigDecimal getDoanhThuHomNay() {
        return chuyenDiRepository.getDoanhThuHomNay();
    }

    // E6: sp_tinh_doanh_thu_chuyen_di_theo_so_ngay
    @Transactional(readOnly = true)
    public List<TongKetNgayDTO> getTongKetTheoSoNgay(int soNgay, LocalDate ngayTinh) {
        return chuyenDiRepository.getTongKetTheoSoNgay(soNgay, ngayTinh);
    }

    // E7: sp_tinh_doanh_thu_chuyen_di_theo_thang
    @Transactional(readOnly = true)
    public List<TongKetThangDTO> getTongKetTheoThang(int thangBD, int thangKT, int nam) {
        return chuyenDiRepository.getTongKetTheoThang(thangBD, thangKT, nam);
    }
    
    @Transactional(readOnly = true)
    public List<DoanhThuTheoNgayDTO> getStatsRevenue(int soNgay) {
        return chuyenDiRepository.getDoanhThuTheoNgay(soNgay);
    }

    @Transactional(readOnly = true)
    public List<ChuyenDiTheoNgayDTO> getStatsTrips(int soNgay) {
        return chuyenDiRepository.getChuyenDiTheoNgay(soNgay);
    }

    @Transactional(readOnly = true)
    public List<TopTaiXeDTO> getStatsTopTaiXe() {
        return chuyenDiRepository.getTopTaiXe();
    }
}