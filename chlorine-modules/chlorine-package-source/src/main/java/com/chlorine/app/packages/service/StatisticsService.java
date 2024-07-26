package com.chlorine.app.packages.service;

import com.chlorine.base.mvc.util.TupleUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.chlorine.app.packages.constant.QTableConstant.qPackageSource;

@Service
public class StatisticsService {

    private final JPAQueryFactory queryFactory;

    protected StatisticsService(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Map<String, Object> statisticsWareHousePackageSource() {
        return statisticsPackageSource(5L);
    }
    public Map<String, Object> statisticsPackageSource(Long packageGroupId) {
        Expression[] fields = {qPackageSource.goodsPrice.sum().as("priceSum"), qPackageSource.weight.sum().as("weightSum"), qPackageSource.jpFreight.sum().as("jpFreightSum"), qPackageSource.count().as("packageCount")};
        Tuple tuple = queryFactory.select(fields).from(qPackageSource).where(qPackageSource.groupId.eq(packageGroupId)).groupBy(qPackageSource.groupId).fetchFirst();
        Map<String, Object> res = TupleUtil.tupleToMap(tuple, fields);
        return res;
    }
}
