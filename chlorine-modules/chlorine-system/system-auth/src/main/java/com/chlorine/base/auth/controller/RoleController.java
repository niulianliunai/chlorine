package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.service.RoleService;
import com.chlorine.base.mvc.controller.BaseController;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.mvc.service.BaseService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "RoleController", value = "角色")
@RestController
@RequestMapping("Role")
public class RoleController extends BaseController<Role> {
    private final RoleService roleService;


    public RoleController(BaseService<Role> service,RoleService roleService) {
        super(service);
        this.roleService = roleService;
    }
}
