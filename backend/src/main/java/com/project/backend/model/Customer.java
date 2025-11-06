package com.project.backend.model;

import jakarta.persistence.*;
import lombok.Data; 

@Entity
@Data
@Table(name = "customers")
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

    // // Mối quan hệ 1-nhiều: Một khách hàng có thể có nhiều chuyến đi
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Trip> trips;
}
