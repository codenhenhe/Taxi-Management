package com.project.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCrypt làm thuật toán mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Cấu hình các quy tắc bảo mật ở đây (ví dụ: /api/dang-ky thì cho phép,
        // /api/admin/** thì yêu cầu xác thực)
        // Đây là ví dụ cơ bản cho phép tất cả (chưa an toàn, chỉ để demo)
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu dùng API (ví dụ: với JWT)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/qtv/dangky", "/api/qtv/dangnhap").permitAll() // Cho phép truy cập 2 đường dẫn này
                .anyRequest().authenticated() // Tất cả các request khác cần xác thực
            );

        return http.build();
    }
}