package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.service.PermissionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "PermissionController", value = "权限")
@RestController
@RequestMapping("permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping("page")
    public CommonResult page(int pageNumber,int pageSize,String contain, Integer menuId) {
        return permissionService.page(pageNumber, pageSize,contain,menuId) ;
    }
    @PostMapping("save")
    public CommonResult save(Permission permission) {
        return permissionService.save(permission);
    }
    @GetMapping("list")
    public CommonResult list() {
        return permissionService.list();
    }
}
