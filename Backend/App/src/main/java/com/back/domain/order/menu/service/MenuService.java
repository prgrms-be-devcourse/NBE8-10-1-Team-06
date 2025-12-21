package com.back.domain.order.menu.service;


import com.back.domain.order.customer.entity.Customer;
import com.back.domain.order.customer.repository.CustomerRepository;
import com.back.domain.order.menu.dto.CreateMenuRequestDto;
import com.back.domain.order.menu.dto.DeleteMenuRequestDto;
import com.back.domain.order.menu.dto.MenuDto;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.repository.MenuRepository;
import com.back.domain.order.order.dto.OrderDto;
import com.back.domain.order.order.entity.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {
    private final MenuRepository menuRepository;
    private final CustomerRepository customerRepository;

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    //이메일 유효성 검사 성공시 true 아니면 false
    @Transactional
    public boolean modify(
            Menu menu,
            String menuName,
            int menuPrice,
            String imageUrl,
            String category,
            String email
    ) {
        if (menu.getCustomer().getEmail().equals(email)) {
            menu.modify(menuName, menuPrice, imageUrl, category);
            return true;
        } else return false;
    }

    public void createMenu(CreateMenuRequestDto req) {
        Customer customer = customerRepository.findByEmail(req.getEmail())
                .orElseGet(() -> customerRepository.save(
                        new Customer(req.getEmail())
                ));
        Menu menu = new Menu(
                customer,
                req.getMenuName(),
                req.getImageURL(),
                req.getPrice(),
                req.getCategory()
        );

        menuRepository.save(menu);
    }

    // 삭제 성공 시 True, 아니면 False return
    public boolean deleteMenu(DeleteMenuRequestDto req) {
        if (req.getMenuId() == null || req.getEmail() == null) return false;
        return menuRepository.deleteByIdAndCustomer_Email(req.getMenuId(), req.getEmail()) == 1;
    }
}
