package com.taximanagement.taxi_management.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

// DTO chuẩn hóa phản hồi lỗi API
public class ErrorResponseDto {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path; // Đường dẫn API xảy ra lỗi

    // Constructors
    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(HttpStatus httpStatus, String message, String path) {
        this(); // Gọi constructor mặc định để lấy timestamp
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    // Getters and Setters (chỉ cung cấp ví dụ Getters)

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}