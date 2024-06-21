package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.service.RoleService;
import com.chlorine.base.mvc.dto.CommonResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "RoleController", value = "角色")
@RestController
@RequestMapping("role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("page")
    public CommonResult page(int pageNumber, int pageSize, String contain) {
        return roleService.page(pageNumber, pageSize, contain);
    }

    @GetMapping("list")
    public CommonResult list() {
        return roleService.list();
    }

    @PostMapping("save")
    public CommonResult save(Role role) {
        return roleService.save(role);
    }
}
