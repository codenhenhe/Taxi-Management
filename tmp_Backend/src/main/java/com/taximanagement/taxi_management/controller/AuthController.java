package com.taximanagement.taxi_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // DTO cho request đăng nhập
import org.springframework.security.authentication.AuthenticationManager; // DTO cho response token
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taximanagement.taxi_management.config.JwtUtil;
import com.taximanagement.taxi_management.dto.AuthRequest;
import com.taximanagement.taxi_management.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint xử lý yêu cầu đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) throws Exception {
        
        // 1. Thực hiện xác thực (authentication)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(), 
                        authenticationRequest.getPassword()
                )
        );

        // 2. Tải thông tin UserDetails
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        // 3. Tạo JWT
        final String jwt = jwtUtil.generateToken(userDetails);

        // 4. Trả về token cho client
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
    // Thường có thêm endpoint cho /register
    // @PostMapping("/register")
    // public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) { /* logic đăng ký */ }
}