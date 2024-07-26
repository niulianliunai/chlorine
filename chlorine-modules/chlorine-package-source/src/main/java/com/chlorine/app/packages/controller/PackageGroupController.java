package com.chlorine.app.packages.controller;

import com.chlorine.app.packages.entity.PackageGroup;
import com.chlorine.app.packages.service.PackageGroupService;
import com.chlorine.base.mvc.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("PackageGroup")
public class PackageGroupController extends BaseController<PackageGroup> {
    public PackageGroupController(PackageGroupService service) {
        super(service);
    }

}
