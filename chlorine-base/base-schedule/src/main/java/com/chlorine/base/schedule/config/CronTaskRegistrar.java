package com.chlorine.base.schedule.config;

import com.chlorine.base.schedule.BaseStarter;
import com.chlorine.base.schedule.entity.ScheduleEntity;
import com.chlorine.base.schedule.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 添加定时任务注册类，用来增加、删除定时任务。
 * @author 81509
 */
@Component
public class CronTaskRegistrar<Entity extends ScheduleEntity> implements DisposableBean {

    private final static Map<Long, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    @Autowired
    private BaseStarter baseStarter;
    @Autowired
    private TaskScheduler taskScheduler;
    private final ScheduleRepository scheduleRepository;

    public CronTaskRegistrar(ScheduleRepository<Entity> scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }


    public static Set<Long> getSchedulerIds() {
        return scheduledTasks.keySet();
    }


    public String  addCronTask(Long id) {
        if (this.scheduledTasks.containsKey(id)) {
            removeCronTask(id);
        }
        Optional<ScheduleEntity> spider = scheduleRepository.findById(id);
        if (spider.isPresent()) {
            spider.get().setIsOpen(1);
            scheduleRepository.save(spider.get());
            ScheduleSchedulingRunnable task = new ScheduleSchedulingRunnable(spider.get(), baseStarter);
            this.scheduledTasks.put(id, scheduleCronTask(new CronTask(task,spider.get().getCron())));
            return "添加成功";
        }
        return "id不存在";

    }

    public boolean contains(Long id) {
        return scheduledTasks.containsKey(id);
    }

    public void removeCronTask(Long id) {
        Optional<ScheduleEntity> spider = scheduleRepository.findById(id);
        if (spider.isPresent()) {
            spider.get().setIsOpen(0);
            scheduleRepository.save(spider.get());
        }
        ScheduledTask scheduledTask = this.scheduledTasks.remove(id);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }

    }

    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }


    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
@AllArgsConstructor
 class ScheduleSchedulingRunnable implements Runnable {

    private final ScheduleEntity scheduleEntity;

    private final BaseStarter baseStarter;

    @Override
    public void run() {
        //执行任务
        baseStarter.start(scheduleEntity);
//        System.out.println("执行任务");
    }
}
