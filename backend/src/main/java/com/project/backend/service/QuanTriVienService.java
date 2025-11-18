package com.project.backend.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.backend.config.JwtTokenUtil;
import com.project.backend.model.QuanTriVien;

import com.project.backend.repository.QuanTriVienRepository;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class QuanTriVienService {

    @Autowired
    private QuanTriVienRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Đăng ký tài khoản mới
    public QuanTriVien dangKy(QuanTriVien qtv) {
        if (repository.existsById(qtv.getMaQuanTriVien())) {
            throw new RuntimeException("Mã quản trị viên đã tồn tại!");
        }
        // Mã hóa mật khẩu
        qtv.setMatKhau(passwordEncoder.encode(qtv.getMatKhau()));
        return repository.save(qtv);
    }

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String dangNhap(String tenDangNhap, String matKhau) {
        Optional<QuanTriVien> qtv = repository.findAll().stream()
                .filter(u -> u.getTenDangNhap().equals(tenDangNhap))
                .findFirst();

        if (qtv.isEmpty()) {
            throw new RuntimeException("Tên đăng nhập không tồn tại!");
        }

        QuanTriVien user = qtv.get();
        if (!passwordEncoder.matches(matKhau, user.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không đúng!");
        }

        // 🔥 Tạo JWT token
        return jwtTokenUtil.generateToken(user.getTenDangNhap());
    }

    // Chỉnh sửa thông tin
    public QuanTriVien capNhatThongTin(String maQtv, QuanTriVien thongTinMoi) {
        QuanTriVien qtv = repository.findById(maQtv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quản trị viên"));

        qtv.setTenQuanTriVien(thongTinMoi.getTenQuanTriVien());
        qtv.setEmail(thongTinMoi.getEmail());
        qtv.setSoDienThoai(thongTinMoi.getSoDienThoai());
        qtv.setNgaySinh(thongTinMoi.getNgaySinh());

        LocalDate ngaySinh = qtv.getNgaySinh().toInstant()           // Chuyển Date -> Instant
                          .atZone(ZoneId.systemDefault()) // Áp dụng múi giờ hệ thống
                          .toLocalDate();    

        if (ngaySinh.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không được nằm trong tương lai");
        }

        // Nếu có mật khẩu mới
        if (thongTinMoi.getMatKhau() != null && !thongTinMoi.getMatKhau().isBlank()) {
            qtv.setMatKhau(passwordEncoder.encode(thongTinMoi.getMatKhau()));
        }

        return repository.save(qtv);
    }
    
    //  Lấy tất cả admin
    public List<QuanTriVien> getAll() {
        return repository.findAll();
    }

    //  Lấy 1 admin theo mã
    public QuanTriVien getOne(String maQuanTriVien) {
        return repository.findById(maQuanTriVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quản trị viên!"));
    }
}
