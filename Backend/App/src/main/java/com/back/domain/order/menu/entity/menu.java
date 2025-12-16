package com.back.domain.order.menu.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class menu extends BaseEntity {

    private String menuName;
    private int price;
}
