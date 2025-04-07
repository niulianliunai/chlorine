package com.chlorine.app.packages.controller;

import com.chlorine.app.packages.entity.PackageSource;
import com.chlorine.app.packages.service.PackageSourceService;
import com.chlorine.base.mvc.controller.BaseController;
import com.chlorine.base.mvc.dto.CommonResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("PackageSource")
public class PackageSourceController extends BaseController<PackageSource> {
    @Autowired
    private PackageSourceService packageSourceService;
    public PackageSourceController(PackageSourceService service) {
        super(service);
    }

    @PostMapping("saveByHtml")
    @ApiOperation("从HTML提取包裹信息")
    public CommonResult saveByHtml(@RequestParam("file") MultipartFile file) {
        return CommonResult.success(packageSourceService.saveByHtml(file));
    }

}
