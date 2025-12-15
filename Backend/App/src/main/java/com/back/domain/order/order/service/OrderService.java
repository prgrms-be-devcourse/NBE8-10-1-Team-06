package com.back.domain.order.order.service;

import com.back.domain.order.order.entity.OrderEntity;
import com.back.domain.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Optional<OrderEntity> findbyid(Long id){
        return orderRepository.findById(id);
    }
}
