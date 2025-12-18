package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "주문 생성 및 내역 조회를 관리합니다.")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "고객 정보를 확인하여 새로운 주문을 등록합니다."
    )
    @ApiResponse(responseCode = "200-1", description = "주문 성공")
    @ApiResponse(responseCode = "400-1", description = "잘못된 요청 데이터")
    @PostMapping("/api/order")
    @Transactional
    public ResponseEntity<OrderDto.CreateResponse> createOrder(
            @Valid @RequestBody OrderDto.CreateRequest request) {

        orderService.createOrder(request);

        return ResponseEntity.ok(
                new OrderDto.CreateResponse("주문이 성공적으로 등록되었습니다.")
        );
    }

    @Operation(
            summary = "주문 내역 조회",
            description = "이메일을 통해 해당 고객의 모든 주문 내역을 조회합니다."
    )
    @ApiResponse(responseCode = "200-1", description = "조회 성공")
    @ApiResponse(responseCode = "404-1", description = "주문 내역 없음")
    @GetMapping("/api/order")
    @Transactional(readOnly = true)
    public ResponseEntity<OrderDto.OrderListResponse> orderList(
            @Parameter(description = "조회할 고객의 이메일", example = "user@example.com")
            @Valid
            @RequestBody OrderDto.OrderListRequest request
    ) {
        OrderDto.OrderListResponse response = orderService.getOrderList(request.email());
        if (response.orders().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }


}
