package com.back.domain.order.order.entity;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.orderitem.entity.OrderItem;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    private LocalDateTime orderTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Customer customer, LocalDateTime orderTime) {
        setCustomer(customer);
        this.orderTime = orderTime;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && !customer.getOrders().contains(this)) {
            customer.getOrders().add(this);
        }
    }
}
