package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.dto.CreateMenuRequestDto;
import com.back.domain.order.menu.dto.DeleteMenuRequestDto;
import com.back.domain.order.menu.dto.MenuDto;
import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.back.domain.order.menu.dto.MenuDto.*;

@Tag(name = "Menu API", description = "메뉴 조회, 생성, 수정, 삭제를 담당하는 API입니다.")
@RequestMapping("/api/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @Operation(summary = "메뉴 전체 조회", description = "현재 등록된 모든 메뉴의 목록을 반환합니다.")
    @GetMapping
    @Transactional(readOnly = true)
    public List<MenuListResponse> getMenus() {
        List<Menu> menus = menuService.findAll();
        return menus
                .stream()
                .map(MenuListResponse::new)
                .toList();
    }

    @Operation(summary = "메뉴 수정", description = "ID를 이용해 기존 메뉴 정보를 수정합니다.")
    @PutMapping("/modify/{id}")
    public ResponseEntity<RsData<MenuModifyResponse>> modifyMenu(
            @Parameter(description = "수정할 메뉴의 고유 ID")
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
        RsData<MenuModifyResponse> rs = new RsData<>(
                "200-1",
                "메뉴를 수정하였습니다.",
                new MenuModifyResponse(menu)
        );

        return ResponseEntity.ok(rs);

    }


    @Operation(summary = "메뉴 생성", description = "새로운 메뉴 정보를 등록합니다.")
    @PostMapping("/api/menu") //추가기능
    @Transactional
    public ResponseEntity<String> createMenu(
            @RequestBody CreateMenuRequestDto req
    ) {
        menuService.createMenu(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("생성 완료되었습니다.");
    }

    @Operation(summary = "메뉴 삭제", description = "ID와 권한 확인을 통해 메뉴를 삭제합니다.")
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
