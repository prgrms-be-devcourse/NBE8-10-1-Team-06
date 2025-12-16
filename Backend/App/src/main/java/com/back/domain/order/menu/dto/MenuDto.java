package com.back.domain.order.menu.dto;

import com.back.domain.order.menu.entity.Menu;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MenuDto {

    public record MenuReadResponse(
            @JsonProperty("menu_id")
            int menuId,
            String category,
            @JsonProperty("menu_name")
            String menuName,
            @JsonProperty("menu_price")
            int menuPrice,
            @JsonProperty("img_url")
            String imgUrl
    ) {
        public MenuReadResponse(Menu menu) {
            this(
                    menu.getId(),
                    menu.getCategory(),
                    menu.getMenuName(),
                    menu.getMenuPrice(),
                    menu.getImgUrl()
            );
        }
    }


}
