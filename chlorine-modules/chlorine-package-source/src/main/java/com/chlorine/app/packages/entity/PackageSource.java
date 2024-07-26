package com.chlorine.app.packages.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Table(name = "app_package_source")
@Entity
public class PackageSource extends BaseEntity {
    private String name;
    private String description;
    private String url;
    private String code;
    private BigDecimal goodsPrice;
    private Integer goodsCount;
    private BigDecimal jpFreight;
    @Transient
    private BigDecimal weightRate;
    @Transient
    private BigDecimal computedPrice;
    private Integer weight;
    private Integer height;
    private Integer width;
    private BigDecimal length;
    private String imageURL;
    @Column(name="group_id")
    private Long groupId;
    @ManyToOne
    @JoinColumn(name = "group_id",insertable = false, updatable = false)
    @JsonIgnoreProperties({"packageSources"})
    private PackageGroup packageGroup;
}
