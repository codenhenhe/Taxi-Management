package com.taximanagement.taxi_management.controller;

import com.taximanagement.taxi_management.model.User;
import com.taximanagement.taxi_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// @PreAuthorize("hasRole('ADMIN')") // Có thể áp dụng cho toàn bộ Controller nếu muốn 
public class AdminController {

    private final UserService userService; // Giả định có UserService để quản lý User

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint này chỉ cho phép truy cập bởi người dùng có quyền ROLE_ADMIN.
     * Trả về danh sách tất cả người dùng trong hệ thống.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<List<User>> getAllUsers() {
        // Gọi service để lấy tất cả người dùng
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint quản lý (khóa/mở khóa) một tài khoản người dùng cụ thể.
     */
    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long userId, @RequestParam boolean isActive) {
        // Gọi service để cập nhật trạng thái
        userService.updateUserActiveStatus(userId, isActive);
        return ResponseEntity.ok("User status updated successfully for ID: " + userId);
    }
    
    // Thêm các endpoint quản trị khác, ví dụ: thống kê, quản lý cấu hình hệ thống...
}