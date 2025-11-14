package com.taximanagement.taxi_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO cho yêu cầu đăng nhập (username và password)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    
    // Tên đăng nhập (có thể là email hoặc username)
    private String username;
    
    // Mật khẩu
    private String password;
}