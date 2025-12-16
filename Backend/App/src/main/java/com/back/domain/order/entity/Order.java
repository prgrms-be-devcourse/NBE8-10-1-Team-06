package com.back.domain.order.entity;

import com.back.domain.customer.entity.Customer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
    private LocalDateTime orderTime;
}
