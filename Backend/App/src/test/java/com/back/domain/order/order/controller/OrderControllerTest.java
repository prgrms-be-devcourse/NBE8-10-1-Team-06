package com.back.domain.order.order.controller;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderitem.entity.OrderItem;
import com.back.domain.order.orderitem.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private MenuRepository menuRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Long menu1Id;
    private Long menu2Id;

    @BeforeEach
    void setUp() {
        Menu menu1 = menuRepository.save(
                new Menu("아메리카노", "tmpImgURL", 4500, "음료", "example@example.com"));
        Menu menu2 = menuRepository.save(
                new Menu("카페라떼", "tmpImgURL", 5000, "음료", "example@example.com"));

        menu1Id = menu1.getId();
        menu2Id = menu2.getId();
    }

    @Test
    @DisplayName("주문 생성 성공 - 신규 고객")
    void t1() throws Exception {
        String requestBody = String.format("""
                {
                    "email": "newcustomer@test.com",
                    "address": "서울시 강남구",
                    "postcode": 12345,
                    "items": [
                        {
                            "menuId": %d,
                            "count": 2
                        },
                        {
                            "menuId": %d,
                            "count": 1
                        }
                    ]
                }
                """, menu1Id, menu2Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 등록되었습니다."));

        Customer customer = customerRepository.findByEmail("newcustomer@test.com").orElse(null);
        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo("newcustomer@test.com");

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getAddress()).isEqualTo("서울시 강남구");
        assertThat(orders.get(0).getPostcode()).isEqualTo(12345);

        List<OrderItem> orderItems = orderItemRepository.findAll();
        assertThat(orderItems).hasSize(2);
    }

    @Test
    @DisplayName("주문 생성 성공 - 기존 고객")
    void t2() throws Exception {
        Customer existingCustomer = customerRepository.save(
                new Customer("existing@test.com"));

        String requestBody = String.format("""
                {
                    "email": "existing@test.com",
                    "address": "서울시 송파구",
                    "postcode": 54321,
                    "items": [
                        {
                            "menuId": %d,
                            "count": 3
                        }
                    ]
                }
                """, menu1Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 등록되었습니다."));

        List<Customer> customers = customerRepository.findAll();
        long customerCount = customers.stream()
                .filter(c -> c.getEmail().equals("existing@test.com"))
                .count();
        assertThat(customerCount).isEqualTo(1);

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getCustomer().getId()).isEqualTo(existingCustomer.getId());
    }

    @Test
    @DisplayName("주문 생성 실패 - 이메일 누락")
    void t3() throws Exception {
        String requestBody = String.format("""
                {
                    "address": "서울시 강남구",
                    "postcode": 12345,
                    "items": [
                        {
                            "menuId": %d,
                            "count": 2
                        }
                    ]
                }
                """, menu1Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 유효하지 않은 이메일 형식")
    void t4() throws Exception {
        String requestBody = String.format("""
                {
                    "email": "invalidemail",
                    "address": "서울시 강남구",
                    "postcode": 12345,
                    "items": [
                        {
                            "menuId": %d,
                            "count": 2
                        }
                    ]
                }
                """, menu1Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 주소 누락")
    void t5() throws Exception {
        String requestBody = String.format("""
                {
                    "email": "test@test.com",
                    "postcode": 12345,
                    "items": [
                        {
                            "menuId": %d,
                            "count": 2
                        }
                    ]
                }
                """, menu1Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 우편번호 누락")
    void t6() throws Exception {
        String requestBody = String.format("""
                {
                    "email": "test@test.com",
                    "address": "서울시 강남구",
                    "items": [
                        {
                            "menuId": %d,
                            "count": 2
                        }
                    ]
                }
                """, menu1Id);

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 주문 아이템 누락")
    void t7() throws Exception {
        String requestBody = """
                {
                    "email": "test@test.com",
                    "address": "서울시 강남구",
                    "postcode": 12345
                }
                """;

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 메뉴")
    void t8() throws Exception {
        String requestBody = """
                {
                    "email": "test@test.com",
                    "address": "서울시 강남구",
                    "postcode": 12345,
                    "items": [
                        {
                            "menuId": 999999,
                            "count": 2
                        }
                    ]
                }
                """;

        mvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 메뉴입니다: 999999"));
    }
}
