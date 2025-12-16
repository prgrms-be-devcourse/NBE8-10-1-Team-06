package com.back.domain.order.menu.entity;


import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Menu extends BaseEntity {
    String menuName;
    String imgUrl;
    int menuPrice;
}
