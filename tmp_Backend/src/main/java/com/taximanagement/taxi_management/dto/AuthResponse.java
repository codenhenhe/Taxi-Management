package com.taximanagement.taxi_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO chứa JWT token phản hồi sau khi xác thực thành công
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    // JSON Web Token
    private String jwt;
    
    // Có thể thêm loại token (ví dụ: "Bearer")
    private final String tokenType = "Bearer"; 
}