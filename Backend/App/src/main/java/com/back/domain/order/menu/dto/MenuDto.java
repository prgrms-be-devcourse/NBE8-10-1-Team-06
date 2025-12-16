package com.back.domain.order.menu.dto;

import com.back.domain.order.menu.entity.Menu;

import java.time.LocalDateTime;

public record MenuDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String menuName,
        String imgUrl
) {
    public MenuDto(Menu menu) {
        this(
                menu.getId(),
                menu.getCreateDate(),
                menu.getModifyDate(),
                menu.getMenuName(),
                menu.getImgUrl()
        );
    }
}
