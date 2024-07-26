package com.chlorine.base.mvc.entity;

import com.chlorine.base.mvc.annotation.ColumnComment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TreeEntity<T> extends BaseEntity{

    private int sort;
    @Column(columnDefinition = "boolean default false")
    @ColumnComment("隐藏标识")
    private Boolean hiddenFlag;
    @ManyToOne
    @JsonIgnoreProperties("children")
    private T parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("sort asc")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<T> children;
}
