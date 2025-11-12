package com.taximanagement.taxi_management.dto;

// DTO dùng để trả về token JWT sau khi đăng nhập thành công
public class LoginResponse {
    
    private String token;
    private final String type = "Bearer"; // Loại token

    // Constructor
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
    
    // Setters
    public void setToken(String token) {
        this.token = token;
    }
}