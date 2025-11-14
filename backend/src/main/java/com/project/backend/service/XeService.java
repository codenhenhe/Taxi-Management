package com.project.backend.service;

import com.project.backend.dto.LoaiXeDTO;
import com.project.backend.dto.XeDTO;
import com.project.backend.dto.XeRequestDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.model.LoaiXe;

import com.project.backend.dto.XeStatsDTO;

import com.project.backend.model.TrangThaiXe;
import com.project.backend.model.Xe;
import com.project.backend.repository.LoaiXeRepository;
import com.project.backend.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


@Service
public class XeService {

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<XeDTO> getAllXe() {
        List<Xe> danhSachEntity = xeRepository.findAll();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)

                .collect(Collectors.toList());
    }

    public XeDTO getXeById(String id) {
        Xe xeEntity = timXeBangId(id);

        return chuyenSangDTO(xeEntity);
    }

    // --- CÁC HÀM CUD (Nhận DTO, Trả về DTO) ---

    public XeDTO createXe(XeRequestDTO dto) {

        // Kiểm tra biển số trùng (nếu cần)
        if (dto.getBienSoXe() != null && xeRepository.existsByBienSoXe(dto.getBienSoXe())) {
            throw new IllegalArgumentException("Biển số xe đã tồn tại!");
        }

        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // 2. Chuyển DTO -> Entity
        Xe xeMoi = new Xe();
        xeMoi.setBienSoXe(dto.getBienSoXe());
        xeMoi.setMauXe(dto.getMauXe());
        xeMoi.setNamSanXuat(dto.getNamSanXuat());
        xeMoi.setLoaiXe(loaiXe);

        // 3. Tạo ID duy nhất: XE-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaXe();
        xeMoi.setMaXe(newId);

        // 4. Set trạng thái mặc định
        xeMoi.setTrangThaiXe(TrangThaiXe.SAN_SANG);

        // 5. Lưu Entity
        Xe xeDaLuu = xeRepository.save(xeMoi);

        // 6. Trả về DTO

        return chuyenSangDTO(xeDaLuu);
    }

    public XeDTO updateXe(String id, XeRequestDTO dto) {

        Xe xeHienTai = timXeBangId(id);

        // Kiểm tra biển số trùng (ngoại trừ chính nó)
        if (dto.getBienSoXe() != null
                && !dto.getBienSoXe().equals(xeHienTai.getBienSoXe())
                && xeRepository.existsByBienSoXe(dto.getBienSoXe())) {
            throw new IllegalArgumentException("Biển số xe đã được sử dụng!");
        }

        // Tìm LoaiXe mới
        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe: " + dto.getMaLoai()));

        // Cập nhật thông tin
        xeHienTai.setBienSoXe(dto.getBienSoXe());
        xeHienTai.setMauXe(dto.getMauXe());
        xeHienTai.setNamSanXuat(dto.getNamSanXuat());
        xeHienTai.setTrangThaiXe(dto.getTrangThaiXe());
        xeHienTai.setLoaiXe(loaiXe);

        Xe xeDaCapNhat = xeRepository.save(xeHienTai);

        return chuyenSangDTO(xeDaCapNhat);
    }

    public void deleteXe(String id) {
        Xe xe = timXeBangId(id);
        xeRepository.delete(xe);
    }

    // --- HÀM HELPER (Chuyển đổi DTO) ---

    /**
     * Chuyển Entity Xe -> DTO (có lồng LoaiXeDTO)
     */
    private XeDTO chuyenSangDTO(Xe xeEntity) {
        if (xeEntity == null) return null;


        XeDTO dto = new XeDTO();
        dto.setMaXe(xeEntity.getMaXe());
        dto.setBienSoXe(xeEntity.getBienSoXe());
        dto.setMauXe(xeEntity.getMauXe());
        dto.setNamSanXuat(xeEntity.getNamSanXuat());
        dto.setTrangThaiXe(xeEntity.getTrangThaiXe());

        if (xeEntity.getLoaiXe() != null) {

            dto.setLoaiXe(chuyenLoaiXeSangDTO(xeEntity.getLoaiXe()));
        }

        return dto;
    }

    /**
     * Chuyển LoaiXe -> LoaiXeDTO (chỉ lấy cần thiết)
     */
    private LoaiXeDTO chuyenLoaiXeSangDTO(LoaiXe loaiXeEntity) {
        if (loaiXeEntity == null) return null;


        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(loaiXeEntity.getMaLoai());
        dto.setTenLoai(loaiXeEntity.getTenLoai());
        return dto;
    }

    /**
     * Tìm Xe theo ID (tái sử dụng)
     */
    private Xe timXeBangId(String id) {
        return xeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã XE-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaXe() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "XE" + code;

            if (!xeRepository.existsByMaXe(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã xe duy nhất sau " + MAX_RETRIES + " lần thử.");
    }

    /**
     * Sinh 8 ký tự ngẫu nhiên từ A-Z, 0-9
     */
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    // --- HÀM MỚI CHO THỐNG KÊ (E3) ---
    @Transactional(readOnly = true)
    public List<XeStatsDTO> getXeStats() {
        return xeRepository.getXeStats();
    }
}