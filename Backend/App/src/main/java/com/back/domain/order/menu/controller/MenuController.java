package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.dto.MenuDto;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/api/menu")
    @Transactional(readOnly = true)
    public List<MenuDto> getMenus() {
        List<Menu> menus = menuService.findAll();

        return menus
                .stream()
                .map(MenuDto::new)
                .toList();
    }
}
