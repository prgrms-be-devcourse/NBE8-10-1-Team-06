package com.back.domain.order.order.service;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderitem.entity.OrderItem;
import com.back.domain.order.orderitem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void createOrder(OrderDto.CreateRequest request) {
        // 1. 고객 정보 조회 또는 생성
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> customerRepository.save(
                        new Customer(
                                request.email(),
                                request.address(),
                                request.postcode(),
                                new ArrayList<>()
                        )
                ));

        // 2. 주문 생성
        Order order = new Order(customer, LocalDateTime.now());
        orderRepository.save(order);

        // 3. 주문 항목 생성
        for (OrderDto.OrderItemRequest itemRequest : request.items()) {
            Menu menu = menuRepository.findById(itemRequest.menuId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "존재하지 않는 메뉴입니다: " + itemRequest.menuId()
                    ));

            OrderItem orderItem = new OrderItem(
                    order,
                    menu,
                    itemRequest.count()
            );
            orderItemRepository.save(orderItem);
        }
    }
}