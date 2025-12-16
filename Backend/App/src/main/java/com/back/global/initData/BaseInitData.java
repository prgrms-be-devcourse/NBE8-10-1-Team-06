package com.back.global.initData;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.entity.OrderEntity;
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


        Customer customer1 = new Customer(null, "test1@example.com", "서울시 강남구 테헤란로 123", 12345, new ArrayList<>());
        customerRepository.save(customer1);

        Customer customer2 = new Customer(null, "test2@example.com", "서울시 서초구 서초대로 456", 54321, new ArrayList<>());
        customerRepository.save(customer2);


        menu menu1 = new menu(null, "아메리카노", 4500);
        menuRepository.save(menu1);

        menu menu2 = new menu(null, "카페라떼", 5000);
        menuRepository.save(menu2);

        menu menu3 = new menu(null, "카푸치노", 5500);
        menuRepository.save(menu3);

        OrderEntity order1 = new OrderEntity(null, customer1, LocalDateTime.now());
        orderRepository.save(order1);

        OrderEntity order2 = new OrderEntity(null, customer2, LocalDateTime.now().minusHours(1));
        orderRepository.save(order2);

        OrderItem orderItem1 = new OrderItem(null, order1, menu1, 2);
        orderItemRepository.save(orderItem1);

        OrderItem orderItem2 = new OrderItem(null, order1, menu2, 1);
        orderItemRepository.save(orderItem2);

        OrderItem orderItem3 = new OrderItem(null, order2, menu3, 3);
        orderItemRepository.save(orderItem3);

    }

}
