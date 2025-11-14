package com.project.backend.service;

import com.project.backend.dto.BangGiaDTO;
import com.project.backend.dto.BangGiaRequestDTO;
import com.project.backend.dto.LoaiXeDTO;
import com.project.backend.exception.ResourceNotFoundException;

import com.project.backend.model.BangGia;
import com.project.backend.model.LoaiXe;
import com.project.backend.repository.BangGiaRepository;
import com.project.backend.repository.LoaiXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BangGiaService {

    @Autowired
    private BangGiaRepository bangGiaRepository;

    @Autowired
    private LoaiXeRepository loaiXeRepository;

    // --- CÁC HÀM GET (Trả về DTO) ---

    public List<BangGiaDTO> getAllBangGia() {
        List<BangGia> danhSachEntity = bangGiaRepository.findAllWithLoaiXe();
        return danhSachEntity.stream()
                .map(this::chuyenSangDTO)

                .collect(Collectors.toList());
    }

    public BangGiaDTO getBangGiaById(String id) {
        BangGia entity = timBangGiaBangId(id, true);

        return chuyenSangDTO(entity);
    }

    // --- CÁC HÀM CUD (Nhận RequestDTO, Trả về DTO) ---

    public BangGiaDTO createBangGia(BangGiaRequestDTO dto) {
        // 1. Tìm LoaiXe

        LoaiXe loaiXe = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + dto.getMaLoai()));

        // 2. Chuyển DTO -> Entity
        BangGia bangGiaMoi = new BangGia();
        bangGiaMoi.setGiaKhoiDiem(dto.getGiaKhoiDiem());
        bangGiaMoi.setGiaTheoKm(dto.getGiaTheoKm());
        bangGiaMoi.setPhuThu(dto.getPhuThu());
        bangGiaMoi.setLoaiXe(loaiXe);

        // 3. Tạo ID duy nhất: BG-XXXXXXXX (8 ký tự ngẫu nhiên, kiểm tra trùng)
        String newId = generateUniqueMaBangGia();
        bangGiaMoi.setMaBangGia(newId);

        // 4. Lưu vào DB
        BangGia bangGiaDaLuu = bangGiaRepository.save(bangGiaMoi);

        // 5. Trả về DTO

        return chuyenSangDTO(bangGiaDaLuu);
    }

    public BangGiaDTO updateBangGia(String id, BangGiaRequestDTO dto) {
        BangGia bangGiaHienTai = timBangGiaBangId(id, false);

        LoaiXe loaiXeMoi = loaiXeRepository.findById(dto.getMaLoai())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe với ID: " + dto.getMaLoai()));

        bangGiaHienTai.setGiaKhoiDiem(dto.getGiaKhoiDiem());
        bangGiaHienTai.setGiaTheoKm(dto.getGiaTheoKm());
        bangGiaHienTai.setPhuThu(dto.getPhuThu());
        bangGiaHienTai.setLoaiXe(loaiXeMoi);

        BangGia bangGiaDaCapNhat = bangGiaRepository.save(bangGiaHienTai);

        return chuyenSangDTO(bangGiaDaCapNhat);
    }

    public void deleteBangGia(String id) {
        BangGia bg = timBangGiaBangId(id, false);

        bangGiaRepository.delete(bg);
    }

    // --- HÀM HELPER (Hàm hỗ trợ) ---

    private BangGiaDTO chuyenSangDTO(BangGia entity) {
        if (entity == null) return null;


        BangGiaDTO dto = new BangGiaDTO();
        dto.setMaBangGia(entity.getMaBangGia());
        dto.setGiaKhoiDiem(entity.getGiaKhoiDiem());
        dto.setGiaTheoKm(entity.getGiaTheoKm());
        dto.setPhuThu(entity.getPhuThu());

        if (entity.getLoaiXe() != null) {
            dto.setLoaiXe(chuyenLoaiXeSangDTO(entity.getLoaiXe()));
        }

        return dto;
    }


    private LoaiXeDTO chuyenLoaiXeSangDTO(LoaiXe loaiXeEntity) {
        if (loaiXeEntity == null) return null;


        LoaiXeDTO dto = new LoaiXeDTO();
        dto.setMaLoai(loaiXeEntity.getMaLoai());
        dto.setTenLoai(loaiXeEntity.getTenLoai());
        return dto;
    }

    private BangGia timBangGiaBangId(String id, boolean useJoinFetch) {
        Optional<BangGia> optionalBg = useJoinFetch
                ? bangGiaRepository.findByIdWithLoaiXe(id)
                : bangGiaRepository.findById(id);

        return optionalBg.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bảng giá với ID: " + id));
    }

    // --- PHẦN SINH ID DUY NHẤT (MỚI) ---

    private static final SecureRandom random = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_RETRIES = 10;

    /**
     * Sinh mã BG-XXXXXXXX duy nhất (8 ký tự A-Z, 0-9)
     * Kiểm tra trùng trong DB → đảm bảo 100% không trùng
     */
    private String generateUniqueMaBangGia() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String code = generateRandomCode();
            String fullId = "BG" + code;

            if (!bangGiaRepository.existsByMaBangGia(fullId)) {
                return fullId;
            }
        }
        throw new RuntimeException("Không thể tạo mã bảng giá duy nhất sau " + MAX_RETRIES + " lần thử.");
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

}