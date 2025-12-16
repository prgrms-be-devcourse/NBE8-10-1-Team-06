package com.back.domain.order.menu.dto;


import lombok.Getter;

@Getter
public class CreateMenuRequestDto {
    String email;
    String category;
    String menuName;
    int price;
    String imageURL;
}
