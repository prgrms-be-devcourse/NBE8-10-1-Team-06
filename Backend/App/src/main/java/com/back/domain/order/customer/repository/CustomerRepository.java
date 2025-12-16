package com.back.domain.order.customer.repository;

import com.back.domain.order.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}
