package com.back.domain.order.orderitem.entity;

import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(length = 100, nullable = false)
    private String menuNameSnapshot;

    @Column(nullable = false)
    private int priceSnapshot;

    @Column(nullable = false)
    private int count;

    public OrderItem(Order order, Menu menu, int count) {
        this.order = order;
        this.menu = menu;
        this.menuNameSnapshot = menu.getMenuName();
        this.priceSnapshot = menu.getMenuPrice();
        this.count = count;
    }
}
