package com.back.domain.order.menu.dto;

import com.back.domain.order.menu.entity.Menu;


public record MenuDto(
        int menuId,
        String menuName,
        int menuPrice,
        String imgUrl
) {
    public MenuDto(Menu menu) {
        this(
                menu.getId(),
                menu.getMenuName(),
                menu.getMenuPrice(),
                menu.getImgUrl()
        );
    }
}
