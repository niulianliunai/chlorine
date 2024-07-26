package com.chlorine.base.schedule.config;

import com.chlorine.base.schedule.repository.ScheduleRepository;
import com.chlorine.base.schedule.entity.ScheduleEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class ScheduleConfig<Entity extends ScheduleEntity> implements CommandLineRunner {


    private final CronTaskRegistrar<Entity> taskRegistrar;

    private final ScheduleRepository<Entity> scheduleRepository;

    public ScheduleConfig(CronTaskRegistrar<Entity> taskRegistrar,ScheduleRepository<Entity> spiderRepository) {
        this.taskRegistrar = taskRegistrar;
        this.scheduleRepository = spiderRepository;
    }

    @Scheduled(cron = "0 0 0/12 * * ? ")
    public void schedule() {
        this.run();
        System.out.println("schedule");
    }

    @Override
    public void run(String... args) {
        List<Entity> scheduleEntities = scheduleRepository.findAll();
        for (Entity schedule : scheduleEntities) {
            if (schedule.getIsOpen() == 1 && !taskRegistrar.contains(schedule.getId())) {
                taskRegistrar.addCronTask(schedule.getId());
            }
        }
    }

}

