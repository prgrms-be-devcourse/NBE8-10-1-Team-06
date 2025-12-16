package com.back.domain.order.menu.service;


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

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public List<Menu> findAll(){
        return menuRepository.findAll();
    }


    public void createMenu(CreateMenuRequestDto req) {
        Menu menu = new Menu(req.getMenuName(),req.getImageURL(),req.getPrice(),req.getCategory(),req.getEmail());
        menuRepository.save(menu);
    }

    // 삭제 성공 시 True, 아니면 False return
    public boolean deleteMenu(DeleteMenuRequestDto req) {
        //TODO : 삭제 로직 구현

        return true;
    }
}
