package com.chlorine.app.packageSource.service;

import com.chlorine.app.packageSource.entity.PackageGroup;
import com.chlorine.app.packageSource.repository.PackageGroupRepository;
import com.chlorine.app.packageSource.entity.PackageSource;
import com.chlorine.app.packageSource.repository.PackageSourceRepository;
import com.chlorine.base.mvc.service.BaseService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PackageSourceService extends BaseService<PackageSource> {

    private final PackageSourceRepository packageSourceRepository;
    private final PackageGroupService packageGroupService;
    private final PackageGroupRepository packageGroupRepository;
    private final JPAQueryFactory queryFactory;

    protected PackageSourceService(PackageSourceRepository PackageSourceRepository,
                                   PackageSourceRepository packageSourceRepository, PackageGroupService packageGroupService, PackageGroupRepository packageGroupRepository, JPAQueryFactory queryFactory) {
        super(PackageSourceRepository);
        this.packageSourceRepository = packageSourceRepository;
        this.packageGroupService = packageGroupService;
        this.packageGroupRepository = packageGroupRepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PackageSource> page(PackageSource entity) {
        packageGroupService.computeGroupTotalPrice();
        Page<PackageSource> packageSources = repository.findAll(buildSpecification(entity), PageRequest.of(entity.getPageNumber() - 1, entity.getPageSize()));
        packageSources.getContent().stream().filter(item -> item.getPackageGroup().getInternationalFreight() != null && item.getPackageGroup().getWeight() != null).forEach(packageSource -> {
            PackageGroup packageGroup = packageSource.getPackageGroup();
            double weightRate = 1.0 * packageSource.getWeight() / packageGroup.getWeight();
            double goodsPrice = packageSource.getGoodsPrice().doubleValue();
            double tariff = goodsPrice * 0.13 + 200 + 300;
            double jpFreight = packageSource.getJpFreight().doubleValue();
            double internationalFreight = packageGroup.getInternationalFreight().doubleValue() * weightRate;
            double total = goodsPrice + tariff + jpFreight + internationalFreight;
            double computedPrice = total / packageSource.getGoodsCount() / packageGroup.getExchangeRate().doubleValue();
            packageSource.setComputedPrice(BigDecimal.valueOf(computedPrice).setScale(2, RoundingMode.HALF_DOWN));
        });
        return packageSources;
    }
}
