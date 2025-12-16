package com.back.domain.order.orderitem.entity;

import com.back.domain.order.menu.entity.menu;
import com.back.domain.order.order.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private menu menu;

    private int count;
}
