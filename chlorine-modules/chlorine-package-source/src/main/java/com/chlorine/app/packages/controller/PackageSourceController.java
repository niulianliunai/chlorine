package com.chlorine.app.packages.controller;

import com.chlorine.app.packages.entity.PackageSource;
import com.chlorine.app.packages.service.PackageSourceService;
import com.chlorine.base.mvc.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("PackageSource")
public class PackageSourceController extends BaseController<PackageSource> {
    public PackageSourceController(PackageSourceService service) {
        super(service);
    }
}
