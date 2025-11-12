package com.taximanagement.taxi_management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity cơ sở cho tất cả người dùng hệ thống (Admin, Driver, Customer)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Đã được mã hóa (hashed)

    @Column(unique = true)
    private String email;

    // Vai trò của người dùng: ADMIN, DRIVER, CUSTOMER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; 

    private LocalDateTime createdAt;
    
    // Enum Role (có thể được định nghĩa trong file riêng)
    public enum Role {
        ADMIN, DRIVER, CUSTOMER
    }
}