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
    String category;
    String email;
    int menuPrice;

    public void modify(
            String menuName,
            int menuPrice,
            String imageUrl
    ) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.imgUrl = imageUrl;
    }
}
