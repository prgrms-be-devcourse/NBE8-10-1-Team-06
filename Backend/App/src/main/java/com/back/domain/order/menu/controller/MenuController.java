package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.back.domain.order.menu.dto.MenuDto.*;

@RequestMapping("/api/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<MenuListResponse> getMenus() {
        List<Menu> menus = menuService.findAll();

        return menus
                .stream()
                .map(MenuListResponse::new)
                .toList();
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<MenuModifyResponse> modifyMenu(
            @PathVariable
            long id,
            @Valid
            @RequestBody
            MenuModifyRequest req
    ) {
        Menu menu = menuService
                .findById(id).get();
        menuService.modify(
                menu,
                req.menuName(),
                req.menuPrice(),
                req.imgUrl()
                );
        MenuModifyResponse rs = new MenuModifyResponse(menu);

        return ResponseEntity.ok(rs);
    }

}
