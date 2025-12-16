package com.back.domain.order.menu.controller;

import com.back.domain.order.menu.entity.Menu;
import com.back.domain.order.menu.service.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MenuControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 전체 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/menu")
                )
                .andDo(print());

        List<Menu> menus = menuService.findAll();

        resultActions
                .andExpect(handler().handlerType(MenuController.class))
                .andExpect(handler().methodName("getMenus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(menus.size()));

        for (int i = 0; i < menus.size(); i++) {
            Menu menu = menus.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].menu_id".formatted(i)).value(menu.getId()))
                    .andExpect(jsonPath("$[%d].menu_name".formatted(i)).value(menu.getMenuName()))
                    .andExpect(jsonPath("$[%d].menu_price".formatted(i)).value(menu.getMenuPrice()))
                    .andExpect(jsonPath("$[%d].img_url".formatted(i)).value(menu.getImgUrl()))
                    .andExpect(jsonPath("$[%d].category".formatted(i)).value(menu.getCategory()));
        }
    }
}