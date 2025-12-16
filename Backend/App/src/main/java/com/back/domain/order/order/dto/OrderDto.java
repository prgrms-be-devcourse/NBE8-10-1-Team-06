package com.back.domain.order.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class OrderDto {
    public record CreateOrderRequest(
            @NotBlank String email,
            @NotBlank String category,
            @JsonProperty("menu_name") @NotBlank String menuName,
            @Positive int price
    ) {}

    public record CreateOrderResponse(
            Long menuId,
            String email,
            String category,
            @JsonProperty("menu_name") String menuName,
            int price
    ) {}
}
