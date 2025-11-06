package com.project.backend.repository;
import com.project.backend.model.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface này tự động kế thừa các hàm CRUD cơ bản
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Không cần viết code thực thi ở đây
}
