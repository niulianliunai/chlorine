package com.chlorine.base.auth.entity;

import com.chlorine.base.mvc.annotation.ColumnComment;
import com.chlorine.base.mvc.annotation.TableComment;
import com.chlorine.base.mvc.entity.BaseEntity;
import com.chlorine.base.mvc.entity.TreeEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
*
* @author chenlong
* @date 2020/12/8
*/
@Getter
@Setter
@Entity
@Table(name = "system_menu")
@Where(clause = "deleted<>1")
@TableComment("用户菜单")
public class Menu extends TreeEntity<Menu> {
    @ColumnComment("菜单名称")
    private String name;
    @ColumnComment("菜单显示名称")
    private String title;
    @ColumnComment("菜单路径")
    private String path;
    @ColumnComment("菜单组件路径")
    private String componentPath;
    @ColumnComment("重定向路径")
    private String redirectPath;

}
