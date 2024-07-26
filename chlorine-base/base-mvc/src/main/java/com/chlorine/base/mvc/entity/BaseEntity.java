package com.chlorine.base.mvc.entity;

import com.chlorine.base.mvc.annotation.ColumnComment;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedDate
    @Column( updatable = false)
    @ColumnComment("创建时间")
    protected Long createTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @LastModifiedDate
    @ColumnComment("更新时间")
    private Long updateTime;

    @Column(columnDefinition = " int(11) default 0 ")
    private Integer version;

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
    @ColumnComment("逻辑删除标识")
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
