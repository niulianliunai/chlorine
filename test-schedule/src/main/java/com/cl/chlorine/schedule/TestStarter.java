package com.cl.chlorine.schedule;

import com.cl.chlorine.entity.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestStarter extends BaseStarter<TestEntity> {
    @Override
    public void start(TestEntity entity) {
        System.out.println(111);
    }
}
