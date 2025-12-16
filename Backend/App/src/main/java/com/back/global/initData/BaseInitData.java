package com.back.global.initData;

import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.entity.Menu;
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

        // Menu
        Menu menu1 = new Menu("아메리카노","tmpImgURL", 4500,"음료","example@example.com");
        menuRepository.save(menu1);

        Menu menu2 = new Menu("카페라떼","tmpImgURL", 5000, "음료","example@example.com");
        menuRepository.save(menu2);

        Menu menu3 = new Menu("카푸치노","tmpImgURL", 5500, "음료","example@example.com");
        menuRepository.save(menu3);
    }

}
