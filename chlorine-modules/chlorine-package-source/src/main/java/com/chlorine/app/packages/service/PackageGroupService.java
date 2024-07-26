package com.chlorine.app.packages.service;

import com.chlorine.app.packages.entity.PackageGroup;
import com.chlorine.app.packages.entity.PackageSource;
import com.chlorine.app.packages.repository.PackageGroupRepository;
import com.chlorine.base.mvc.service.BaseService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PackageGroupService extends BaseService<PackageGroup> {
    protected PackageGroupService(PackageGroupRepository PackageGroupRepository) {
        super(PackageGroupRepository);
    }


    public void computeGroupTotalPrice() {
        List<PackageGroup> packageGroupList = findByField("status", "0");
        packageGroupList.forEach(packageGroup -> {
            AtomicInteger totalPrice = new AtomicInteger();
            List<PackageSource> packageSources = packageGroup.getPackageSources();
            packageSources.forEach(packageSource -> {
                totalPrice.getAndAdd(packageSource.getGoodsPrice().intValue());
            });
            packageGroup.setTotalPrice(BigDecimal.valueOf(totalPrice.get()));
        });
        saveAll(packageGroupList);
    }
}
