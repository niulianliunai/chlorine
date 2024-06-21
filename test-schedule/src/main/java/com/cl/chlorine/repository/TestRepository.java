package com.cl.chlorine.repository;

import com.cl.chlorine.entity.TestEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface TestRepository extends ScheduleRepository<TestEntity> {
}
