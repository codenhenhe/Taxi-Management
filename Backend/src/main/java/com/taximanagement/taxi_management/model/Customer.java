package com.taximanagement.taxi_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Entity Khách hàng
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String phone;
    
    // Địa chỉ mặc định hoặc địa chỉ liên hệ
    private String address;

    // Mối quan hệ 1-nhiều: Một khách hàng có thể có nhiều chuyến đi
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;
}