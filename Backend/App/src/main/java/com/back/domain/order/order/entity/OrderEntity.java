package com.back.domain.order.order.entity;

import com.back.domain.order.customer.entity.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class OrderEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    @ManyToOne(fetch = LAZY)
    private Customer customer;
    private LocalDateTime orderTime;
}
