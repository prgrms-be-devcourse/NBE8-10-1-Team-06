package com.back.domain.order.menu.dto;

import com.back.domain.order.menu.entity.Menu;
import com.fasterxml.jackson.annotation.JsonProperty;


public class MenuDto {

    public record MenuListResponse(
            @JsonProperty("menu_id")
            long menuId,
            String category,
            @JsonProperty("menu_name")
            String menuName,
            @JsonProperty("menu_price")
            int menuPrice,
            @JsonProperty("img_url")
            String imgUrl
    ) {
        public MenuListResponse(Menu menu) {
            this(
                    menu.getId(),
                    menu.getCategory(),
                    menu.getMenuName(),
                    menu.getMenuPrice(),
                    menu.getImgUrl()
            );
        }
    }

    public record MenuModifyRequest(
            @JsonProperty("menu_id")
            Long menuId,
            @JsonProperty("menu_name")
            String menuName,
            @JsonProperty("menu_price")
            int menuPrice,
            @JsonProperty("img_url")
            String imgUrl
    ) {}

    public record MenuModifyResponse(
            @JsonProperty("menu_id")
            long menuId,
            @JsonProperty("menu_name")
            String menuName,
            @JsonProperty("menu_price")
            int menuPrice
    ) {
        public MenuModifyResponse(Menu menu) {
            this(
                    menu.getId(),
                    menu.getMenuName(),
                    menu.getMenuPrice()
            );
        }
    }


}
