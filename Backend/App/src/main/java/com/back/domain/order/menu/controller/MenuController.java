package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.dto.CreateMenuRequestDto;
import com.back.domain.order.menu.dto.DeleteMenuRequestDto;
import com.back.domain.order.menu.dto.MenuDto;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<RsData<MenuModifyResponse>> modifyMenu(
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
                req.imgUrl(),
                req.category()
                );
        RsData<MenuModifyResponse> rs = new RsData<>("메뉴를 수정하였습니다.", new MenuModifyResponse(menu));

        return ResponseEntity.ok(rs);

    }


    @PostMapping("/api/menu") //추가기능
    @Transactional
    public ResponseEntity<String> createMenu(
            @RequestBody CreateMenuRequestDto req
    ) {
        menuService.createMenu(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("생성 완료되었습니다.");
    }

    @DeleteMapping("/api/menu/delete/{menu_id}")
    @Transactional
    public ResponseEntity<String> deleteMenu(
            @PathVariable Long menu_id,
            @RequestBody @Valid DeleteMenuRequestDto req
    ) {
        req.setMenuId(menu_id);

        boolean ok = menuService.deleteMenu(req);

        if (true) { //validation 통과
            return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다.");
        }
        // validation 실패
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일이 잘못되었거나 삭제 권한이 없습니다.");
    }
}
