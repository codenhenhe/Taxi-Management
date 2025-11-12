package com.taximanagement.taxi_management.service;

import com.taximanagement.taxi_management.config.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // Sử dụng JwtUtil đã có trong cấu trúc

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String authenticateAndGetToken(String username, String password) {
        // 1. Thực hiện xác thực (Authentication)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2. Thiết lập Authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tạo JWT
        String jwt = jwtUtil.generateToken(authentication);

        return jwt;
    }

    // Có thể thêm logic đăng ký (registerUser) tại đây
}