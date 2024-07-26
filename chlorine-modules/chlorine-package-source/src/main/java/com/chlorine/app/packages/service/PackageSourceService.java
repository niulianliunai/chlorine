package com.chlorine.app.packages.service;

import com.chlorine.app.packages.entity.PackageGroup;
import com.chlorine.app.packages.entity.PackageSource;
import com.chlorine.app.packages.repository.PackageGroupRepository;
import com.chlorine.app.packages.repository.PackageSourceRepository;
import com.chlorine.base.mvc.service.BaseService;
import com.chlorine.base.util.StringUtil;
import com.chlorine.minio.util.MinioUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PackageSourceService extends BaseService<PackageSource> {

    private final PackageSourceRepository packageSourceRepository;
    private final PackageGroupService packageGroupService;
    private final PackageGroupRepository packageGroupRepository;
    private final JPAQueryFactory queryFactory;

    private final MinioUtil minioUtil;

    protected PackageSourceService(PackageSourceRepository PackageSourceRepository,
                                   PackageSourceRepository packageSourceRepository, PackageGroupService packageGroupService, PackageGroupRepository packageGroupRepository, JPAQueryFactory queryFactory, MinioUtil minioUtil) {
        super(PackageSourceRepository);
        this.packageSourceRepository = packageSourceRepository;
        this.packageGroupService = packageGroupService;
        this.packageGroupRepository = packageGroupRepository;
        this.queryFactory = queryFactory;
        this.minioUtil = minioUtil;
    }

    @Override
    public Page<PackageSource> page(PackageSource entity) {
        packageGroupService.computeGroupTotalPrice();
        Page<PackageSource> packageSources = repository.findAll(buildSpecification(entity), PageRequest.of(entity.getPageNumber() - 1, entity.getPageSize()));
        packageSources.getContent().stream().filter(item -> item.getPackageGroup().getInternationalFreight() != null && item.getPackageGroup().getWeight() != null).forEach(packageSource -> {
            try {
                PackageGroup packageGroup = packageSource.getPackageGroup();
                double weightRate = 1.0 * packageSource.getWeight() / packageGroup.getWeight();
                double goodsPrice = packageSource.getGoodsPrice().doubleValue();
                double tariff = goodsPrice * 0.13 + 200 + 300;
                double jpFreight = packageSource.getJpFreight().doubleValue();
                double internationalFreight = packageGroup.getInternationalFreight().doubleValue() * weightRate;
                double total = goodsPrice + tariff + jpFreight + internationalFreight;
                double computedPrice = total / packageSource.getGoodsCount() / packageGroup.getExchangeRate().doubleValue();
                packageSource.setComputedPrice(BigDecimal.valueOf(computedPrice).setScale(2, RoundingMode.HALF_DOWN));

                List<String> img = StringUtil.split(packageSource.getImageURL(), "/");
                if (img.size() > 2) {
                    packageSource.setImageURL(minioUtil.generatePresignedUrl(img.get(0),img.get(1) + "/" + img.get(2)));
                }
            }catch (Exception e) {
                e.printStackTrace();
                packageSource.setDescription(e.getMessage());
            }

        });
        return packageSources;
    }
}
