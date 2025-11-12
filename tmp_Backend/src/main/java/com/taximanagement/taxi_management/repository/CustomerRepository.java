package com.taximanagement.taxi_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taximanagement.taxi_management.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm kiếm khách hàng theo số điện thoại (thường là duy nhất)
    Optional<Customer> findByPhone(String phone);
    
    // Tìm kiếm khách hàng theo họ tên (sử dụng LIKE trong SQL)
    List<Customer> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
}