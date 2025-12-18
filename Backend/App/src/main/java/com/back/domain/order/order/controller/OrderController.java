package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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

        // 주문 최대 수량 100개 제한 (전체 수량 기준)
        int totalCount = request.items()
                .stream()
                .mapToInt(OrderDto.OrderItemRequest::count)
                .sum();

        if (totalCount <= 0 || totalCount > 100) {
            return ResponseEntity
                    .badRequest()
                    .body(new OrderDto.CreateResponse("주문 수량은 1개 이상 100개 이하만 가능합니다."));
        }

        orderService.createOrder(request);

        return ResponseEntity.ok(
                new OrderDto.CreateResponse("주문이 성공적으로 등록되었습니다.")
        );
    }

    @PostMapping("/api/order/list")
    @Transactional(readOnly = true)
    public ResponseEntity<OrderDto.OrderListResponse> orderList(
            @Valid @RequestBody OrderDto.OrderListRequest request
    ) {
        OrderDto.OrderListResponse response = orderService.getOrderList(request.email());
        if (response.orders().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }


}
