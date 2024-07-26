package com.chlorine.base.mvc.controller;

import com.chlorine.base.mvc.entity.TreeEntity;
import com.chlorine.base.mvc.service.TreeService;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;

public class TreeController<Entity extends TreeEntity<Entity>>
        extends BaseController<Entity>{

    private final TreeService<Entity> service;
    public TreeController(TreeService<Entity> service) {
        super(service);
        this.service = service;
    }
    @Override
    @GetMapping("list")
    public CommonResult list() {
        return CommonResult.success(service.listTree());
    }
}
