package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/order")
    @Transactional
    public ResponseEntity<OrderDto.CreateResponse> createOrder(
            @Valid @RequestBody OrderDto.CreateRequest request) {

        orderService.createOrder(request);

        return ResponseEntity.ok(
                new OrderDto.CreateResponse("주문이 성공적으로 등록되었습니다.")
        );
    }

}
