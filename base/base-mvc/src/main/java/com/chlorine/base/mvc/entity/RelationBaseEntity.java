package com.chlorine.base.mvc.entity;

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
public abstract class RelationBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(columnDefinition = "tinyint(4) default 0",updatable = false)
    private Boolean deleted;
}
