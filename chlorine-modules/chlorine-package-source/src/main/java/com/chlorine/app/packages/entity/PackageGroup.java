package com.chlorine.app.packages.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "app_package_group")
// @GenerateMVC
public class PackageGroup extends BaseEntity {
    private String name;
    private String trackingNumber;
    private BigDecimal internationalFreight;
    private BigDecimal exchangeRate;
    private Integer weight;
    private Integer status;
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "packageGroup")
    private List<PackageSource> packageSources;
}
