package com.chlorine.app.packageSource.controller;

import com.chlorine.app.packageSource.entity.PackageGroup;
import com.chlorine.base.mvc.controller.BaseController;
import com.chlorine.app.packageSource.service.PackageGroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("PackageGroup")
public class PackageGroupController extends BaseController<PackageGroup> {
    public PackageGroupController(PackageGroupService service) {
        super(service);
    }

}
