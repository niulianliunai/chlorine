package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.entity.Menu;
import com.chlorine.base.auth.service.MenuService;
import com.chlorine.base.mvc.controller.TreeController;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Menu")
public class MenuController extends TreeController<Menu> {
    private final MenuService menuService;

    public MenuController(MenuService service, MenuService menuService) {
        super(service);
        this.menuService = menuService;
    }

    @GetMapping("listRoleMenu")
    public CommonResult listRoleMenu(Long roleId) {
        return CommonResult.success(menuService.listRoleMenu(roleId));
    }

    @GetMapping("listUserMenu")
    public CommonResult listUserMenu(Long userId) {
        return CommonResult.success(menuService.listUserMenu(userId));
    }
}
