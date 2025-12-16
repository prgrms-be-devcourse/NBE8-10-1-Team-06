package com.back.global.initData;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.entity.Order;
import com.back.domain.order.order.repository.OrderRepository;
import com.back.domain.order.orderitem.entity.OrderItem;
import com.back.domain.order.orderitem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Profile("dev") // dev 프로필에서만 실행
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;

    private final CustomerRepository customerRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner(){
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (customerRepository.count() > 0) return;


        // Customer
        Customer customer1 = new Customer("test1@example.com", "서울시 강남구 테헤란로 123", 12345, new ArrayList<>());
        customerRepository.save(customer1);

        Customer customer2 = new Customer("test2@example.com", "서울시 서초구 서초대로 456", 54321, new ArrayList<>());
        customerRepository.save(customer2);

        // Menu
        menu menu1 = new menu("아메리카노", 4500);
        menuRepository.save(menu1);

        menu menu2 = new menu("카페라떼", 5000);
        menuRepository.save(menu2);

        menu menu3 = new menu("카푸치노", 5500);
        menuRepository.save(menu3);

        // Order
        Order order1 = new Order(customer1, LocalDateTime.now());
        orderRepository.save(order1);

        Order order2 = new Order(customer2, LocalDateTime.now().minusHours(1));
        orderRepository.save(order2);

        // OrderItem
        OrderItem orderItem1 = new OrderItem(order1, menu1, 2);
        orderItemRepository.save(orderItem1);

        OrderItem orderItem2 = new OrderItem(order1, menu2, 1);
        orderItemRepository.save(orderItem2);

        OrderItem orderItem3 = new OrderItem(order2, menu3, 3);
        orderItemRepository.save(orderItem3);
    }

}
