package com.back.domain.order.order.controller;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.menu.service.MenuService;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MenuService menuService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired private MenuRepository menuRepository;

    private Long menu1Id;
    private Long menu2Id;


    @BeforeEach
    void setUpMenus() {
        menuRepository.deleteAll();

        Menu menu1 = menuRepository.save(new Menu("아메리카노","tmpImgURL", 4500,"음료","example@example.com"));
        Menu menu2 = menuRepository.save(new Menu("카페라떼","tmpImgURL", 5000,"음료","example@example.com"));
        menuRepository.save(new Menu("카푸치노","tmpImgURL", 5500,"음료","example@example.com"));

        menu1Id = menu1.getId();
        menu2Id = menu2.getId();
    }

    @Test
    @DisplayName("주문 생성 - 신규 고객")
    void t1() throws Exception {
        String customerEmail = "test@test.com";
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customerEmail);

        ResultActions resultActions = mvc
                .perform(
                        post("/api/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "email": "test@test.com",
                                  "address": "서울시 강남구",
                                  "postcode": 12345,
                                  "items": [
                                    { "menuId": %d, "count": 2 },
                                    { "menuId": %d, "count": 1 }
                                  ]
                                }
                                """.formatted(menu1Id, menu2Id))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 등록되었습니다."));

        Customer customer = customerRepository.findByEmail(customerEmail).orElseThrow();
        assert customer.getOrders().size() > 0;
    }
}
