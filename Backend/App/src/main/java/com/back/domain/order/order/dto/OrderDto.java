package com.back.domain.order.order.dto;

import com.back.domain.order.order.entity.Order;
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

    public record CreateResponse(String message) {}

    public record OrderListRequest(
            @NotNull String email
    ){ //주문 내역을 요청하는 DTO, 이메일을 포함한다

    }

    public record OrderListResponse(
            @NotNull String email,
            @NotNull String address,
            @NotNull int postcode,
            @NotNull List<OrderItemDTO> orders
    ) { //주문 내역을 반환하는 DTO, 주문 내역을 반환한다
    }

    public record OrderItemDTO(
            @NotNull String menuName,
            @NotNull int menuPrice,
            @NotNull int count
    ){

    }
}
