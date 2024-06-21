package com.chlorine.base.schedule.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ScheduleEntity extends BaseEntity {
    private String name;
    private String cron;
    private Integer isOpen;

}
