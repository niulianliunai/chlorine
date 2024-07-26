package com.chlorine.base.schedule.service;

import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.mvc.service.BaseService;
import com.chlorine.base.schedule.config.CronTaskRegistrar;
import com.chlorine.base.schedule.entity.ScheduleEntity;
import com.chlorine.base.schedule.repository.ScheduleRepository;

public class ScheduleService<Entity extends ScheduleEntity> extends BaseService<Entity> {
    private final CronTaskRegistrar<Entity> taskRegistrar;

    protected ScheduleService(ScheduleRepository<Entity> repository, CronTaskRegistrar<Entity> taskRegistrar) {
        super(repository);
        this.taskRegistrar = taskRegistrar;
    }


    public CommonResult getSchedulerIds() {
        return CommonResult.success(taskRegistrar.getSchedulerIds());
    }

    public CommonResult addCronTask(Long id) {
        return CommonResult.success("", taskRegistrar.addCronTask(id));
    }


    public CommonResult removeCronTask(Long id) {
        taskRegistrar.removeCronTask(id);
        return CommonResult.success("", "删除成功");
    }
}
