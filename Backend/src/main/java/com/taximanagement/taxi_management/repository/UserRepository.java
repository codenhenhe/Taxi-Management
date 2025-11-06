package com.taximanagement.taxi_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Phương thức tùy chỉnh để tìm kiếm User bằng username (cần cho Spring Security)
    Optional<User> findByUsername(String username);
    
    // Tìm kiếm User bằng email
    Optional<User> findByEmail(String email);
}