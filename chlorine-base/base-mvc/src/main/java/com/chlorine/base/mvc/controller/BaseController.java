package com.chlorine.base.mvc.controller;

import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.mvc.entity.BaseEntity;
import com.chlorine.base.mvc.service.BaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class BaseController<Entity extends BaseEntity> {
    protected final BaseService<Entity> service;

    public BaseController(BaseService<Entity> service) {
        this.service = service;
    }

    @PostMapping("page")
    @ApiOperation("分页查询")
    public CommonResult page(@RequestBody Entity entity) {
        return CommonResult.success(service.page(entity));
    }

    @GetMapping("list")
    @ApiOperation("查询所有")
    public CommonResult list() {
        return CommonResult.success(service.list());
    }

    @PostMapping("saveAll")
    @ApiOperation("批量新增/修改")
    public CommonResult saveAll(@RequestBody List<Entity> entities) {
        return CommonResult.success(service.saveAll((entities)));
    }

    @PostMapping("save")
    @ApiOperation("新增/修改")
    public CommonResult save(@RequestBody Entity entity) {
        return CommonResult.success(service.save(entity));
    }

    @GetMapping("deleteById")
    @ApiOperation("通过id删除")
    public CommonResult deleteById(Long id) {
        service.deleteById(id);
        return CommonResult.success("", "成功");
    }

    @PostMapping("delete")
    @ApiOperation("通过对象属性删除")
    public CommonResult delete(Entity entity) {
        service.delete(entity);
        return CommonResult.success("", "成功");
    }

    @GetMapping("logicDelete")
    @ApiOperation("通过id删除")
    public CommonResult logicDelete(Long id) {
        service.logicDelete(id);
        return CommonResult.success("", "成功");
    }

}

