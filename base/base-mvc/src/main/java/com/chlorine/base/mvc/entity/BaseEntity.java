package com.chlorine.base.mvc.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedDate
    @Column(name = "create_time", updatable = false)
    protected Long createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private Long updateTime;

    @Transient
    private Integer pageSize;

    @Transient
    private Integer pageNumber;


    @Transient
    private Map<String, Object> params = new HashMap<>();

//    @Transient
//    private List<Predicate> predicateList = new ArrayList<>();
//


//    @CreatedBy
//    @Column(name = "created_by", updatable = false, length = 64)
//    private String createdBy;
//
//    @LastModifiedBy
//    @Column(name = "updated_by", length = 64)
//    private String updatedBy;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean deleted = false;

//    @PrePersist
//    protected void onCreate() {
//        createTime = LocalDateTime.now();
//        updateTime = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updateTime = LocalDateTime.now();
//    }

}
