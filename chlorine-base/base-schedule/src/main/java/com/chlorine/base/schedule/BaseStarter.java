package com.chlorine.base.schedule;


import com.chlorine.base.schedule.entity.ScheduleEntity;


public abstract class BaseStarter<Entity extends ScheduleEntity> {

    public abstract void start(Entity entity);

}
