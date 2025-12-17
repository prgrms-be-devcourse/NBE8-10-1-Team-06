package com.back.domain.order.order.controller;

import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.order.service.OrderService;
import com.back.domain.order.orderitem.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class OrderControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    private Menu menu1;
    private Menu menu2;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();

        menu1 = new Menu("아메리카노", "img1", 3000, "커피", "example@example.com");
        menu2 = new Menu("카페라떼", "img2", 4000, "커피", "example@example.com");
        menuRepository.saveAll(List.of(menu1, menu2));

        // 주문 조회 테스트를 위한 기본 주문 하나 생성
        OrderDto.CreateRequest createRequest = new OrderDto.CreateRequest(
                "order@example.com",
                "서울시 강남구 테헤란로",
                12345,
                List.of(
                        new OrderDto.OrderItemRequest(menu1.getId(), 2),
                        new OrderDto.OrderItemRequest(menu2.getId(), 1)
                )
        );
        orderService.createOrder(createRequest);
    }

    @Test
    @DisplayName("주문 생성 - POST /api/order")
    void t00_createOrder() throws Exception {
        // given
        String email = "test-create@example.com";

        String json = """
                {
                  "email": "%s",
                  "address": "서울시 서초구 서초대로",
                  "postcode": 54321,
                  "items": [
                    { "menuId": %d, "count": 1 },
                    { "menuId": %d, "count": 3 }
                  ]
                }
                """.formatted(email, menu1.getId(), menu2.getId());

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 등록되었습니다."));

        // DB에 주문/주문상품이 잘 저장되었는지 간단 검증
        assertThat(orderRepository.findByCustomerEmail(email)).hasSize(1);
        assertThat(orderItemRepository.findByOrderCustomerEmail(email)).hasSize(2);
    }

    @Test
    @DisplayName("주문 내역 조회 - GET /api/order")
    void t01_getOrderList() throws Exception {
        // given
        String email = "order@example.com";
        String json = """
                {
                  "email": "%s"
                }
                """.formatted(email);

        OrderDto.OrderListResponse expected = orderService.getOrderList(email);

        // when
        ResultActions resultActions = mvc
                .perform(
                        get("/api/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("orderList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(expected.email()))
                .andExpect(jsonPath("$.address").value(expected.address()))
                .andExpect(jsonPath("$.postcode").value(expected.postcode()))
                .andExpect(jsonPath("$.orders.length()").value(expected.orders().size()))
                .andExpect(jsonPath("$.orders[0].menuName").value(expected.orders().get(0).menuName()))
                .andExpect(jsonPath("$.orders[0].menuPrice").value(expected.orders().get(0).menuPrice()))
                .andExpect(jsonPath("$.orders[0].count").value(expected.orders().get(0).count()));
    }
}

