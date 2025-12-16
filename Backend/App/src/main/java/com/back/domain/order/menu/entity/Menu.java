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
    String category;
    String email;

    public Menu(String menuName, String imgUrl, int menuPrice, String category, String email) {
        this.menuName = menuName;
        this.imgUrl = imgUrl;
        this.menuPrice = menuPrice;
        this.category = category;
        this.email = email;
    }
}
