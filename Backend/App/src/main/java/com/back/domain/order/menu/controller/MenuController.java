package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.dto.CreateMenuRequestDto;
import com.back.domain.order.menu.dto.MenuDto;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    @PostMapping("/api/menu") //추가기능
    @Transactional
    public ResponseEntity<String> createMenu(
            @RequestBody CreateMenuRequestDto req
    ) {
        menuService.createMenu(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("생성 완료되었습니다.");
    }




}
