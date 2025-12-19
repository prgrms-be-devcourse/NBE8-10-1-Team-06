package com.back.domain.order.order.controller;

import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order API", description = "카페 주문 생성 및 고객별 주문 내역 조회를 담당하는 API입니다.")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "주문 등록",
            description = "사용자 이메일과 선택한 메뉴 목록을 받아 새로운 주문을 생성합니다. 주문 정보는 시스템에 영구 저장됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200-1", description = "주문 완료 (성공 메시지 반환)",
                    content = @Content(schema = @Schema(implementation = OrderDto.CreateResponse.class))),
            @ApiResponse(responseCode = "400-1", description = "요청 데이터 유효성 검사 실패")
    })
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
            summary = "고객별 주문 내역 조회",
            description = "입력된 이메일 주소로 등록된 모든 주문 내역을 시간 역순으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200-1", description = "내역 조회 성공 (주문 리스트 반환)",
                    content = @Content(schema = @Schema(implementation = OrderDto.OrderListResponse.class))),
            @ApiResponse(responseCode = "404-1", description = "해당 이메일로 등록된 주문 내역을 찾을 수 없음")
    })
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
