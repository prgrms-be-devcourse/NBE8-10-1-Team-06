package com.back.domain.order.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderDto {

    public record CreateRequest(
            @NotBlank @Email String email,
            @NotBlank String address,
            @NotNull Integer postcode,
            @NotNull List<OrderItemRequest> items
    ) {
    }

    public record OrderItemRequest(
            @NotNull Long menuId,
            @NotNull int count
    ) {
    }

    public record CreateResponse(String message) {
    }
}
