package com.back.domain.order.menu.entity;

import com.back.domain.order.customer.entity.Customer;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor
public class Menu extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    @NotNull
    private Customer customer;

    @NotNull
    String menuName;

    @NotNull
    String imgUrl;

    int menuPrice;

    @NotNull
    String category;

    public Menu(Customer customer, String menuName, String imgUrl, int menuPrice, String category) {
        setCustomer(customer);
        this.menuName = menuName;
        this.imgUrl = imgUrl;
        this.menuPrice = menuPrice;
        this.category = category;

    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null && !customer.getMenu().contains(this)) {
            customer.getMenu().add(this);
        }
    }

    public void modify(
            String menuName,
            int menuPrice,
            String imageUrl,
            String category
    ) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.imgUrl = imageUrl;
        this.category = category;
    }

}
