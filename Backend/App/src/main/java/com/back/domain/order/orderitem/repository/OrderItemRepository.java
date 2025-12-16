package com.back.domain.order.orderitem.repository;

import com.back.domain.order.orderitem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}