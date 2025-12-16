package com.back.domain.order.customer.entity;

import com.back.domain.order.order.entity.Order;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    private String email;
    private String address;
    private Integer postcode;

    @OneToMany(mappedBy = "customer", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

}
