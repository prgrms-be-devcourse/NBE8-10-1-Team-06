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
    //추가 할때는 이게 필요한데 여기 추가가 맞나?
    String category;
    String email;

}
