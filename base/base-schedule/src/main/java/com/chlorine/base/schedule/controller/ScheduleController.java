package com.chlorine.base.schedule.controller;

import com.chlorine.base.mvc.controller.BaseController;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.schedule.entity.ScheduleEntity;
import com.chlorine.base.schedule.service.ScheduleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

public class ScheduleController<Entity extends ScheduleEntity> extends BaseController<Entity> {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        super(scheduleService);
        this.scheduleService = scheduleService;
    }

    @ApiOperation("添加任务到定时器")
    @GetMapping("addCronTask")
    public CommonResult addCronTask(Long id) {
        return scheduleService.addCronTask(id);
    }

    @ApiOperation("从定时器删除任务")
    @GetMapping("removeCronTask")
    public CommonResult removeCronTask(Long id) {
        return scheduleService.removeCronTask(id);
    }

    @ApiOperation("获取定时器任务ID列表")
    @GetMapping("getSchedulerIds")
    public CommonResult getScheduler() {
        return scheduleService.getSchedulerIds();
    }

}
