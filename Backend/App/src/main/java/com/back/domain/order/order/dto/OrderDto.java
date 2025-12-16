package com.back.domain.order.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderDto {

    public record CreateRequest(
            String email,
            String address,
            Integer postcode
    ) {}
}