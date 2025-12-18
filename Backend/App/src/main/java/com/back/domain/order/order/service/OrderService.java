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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void createOrder(OrderDto.CreateRequest request) {
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> customerRepository.save(
                        new Customer(request.email())
                ));

        Order order = new Order(
                customer,
                LocalDateTime.now(),
                request.address(),
                request.postcode()
        );
        orderRepository.save(order);

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

    @Transactional(readOnly = true)
    public OrderDto.OrderListResponse getOrderList(@NotNull String email) {
        // email을 기준으로 해당 고객의 주문 상품 목록 조회
        List<OrderItem> orderItemList = orderItemRepository.findByOrderCustomerEmail(email);

        if (orderItemList.isEmpty()) {
            // 주문 내역이 없으면 빈 리스트를 반환
            return new OrderDto.OrderListResponse(email, "", 0, List.of());
        }

        Order order = orderItemList.get(0).getOrder();
        Customer customer = order.getCustomer();

        // OrderItem 엔티티 리스트를 OrderItemDTO 리스트로 변환
        List<OrderDto.OrderItemDTO> orders = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            orders.add(new OrderDto.OrderItemDTO(
                    orderItem.getMenu().getMenuName(),
                    orderItem.getMenu().getMenuPrice(),
                    orderItem.getCount()
            ));
        }

        return new OrderDto.OrderListResponse(
                customer.getEmail(),
                order.getAddress(),
                order.getPostcode(),
                orders
        );
    }
}