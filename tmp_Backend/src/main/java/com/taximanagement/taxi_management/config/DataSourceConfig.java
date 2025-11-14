package com.taximanagement.taxi_management.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    /**
     * Cấu hình DataSource chính (Primary) của ứng dụng.
     * Nó đọc các thuộc tính bắt đầu bằng 'spring.datasource' từ application.properties.
     * Sử dụng thư viện HikariCP mặc định của Spring Boot cho connection pooling.
     */
    @Bean
    @Primary // Đánh dấu đây là DataSource mặc định (chính)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    // Ví dụ nếu bạn cần cấu hình một DataSource thứ hai (secondary) cho mục đích đọc/báo cáo riêng biệt:
    /*
    @Bean
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    */
    
    // Bạn cũng có thể tùy chỉnh các thuộc tính của HikariCP nếu cần, 
    // ví dụ: .type(com.zaxxer.hikari.HikariDataSource.class)
}