package com.chlorine.base.auth.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
*
* @author chenlong
* @date 2020/12/8
*/
@Getter
@Setter
@Entity
@Table(name = "system_role")
@Where(clause = "deleted<>1")
public class Role extends BaseEntity {
    private String name;
    @ManyToMany(cascade = CascadeType.PERSIST)
//    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(name = "system_role_menu",joinColumns = {@JoinColumn(name = "role_id")},inverseJoinColumns = {@JoinColumn(name = "menu_id")})
    private Set<Menu> menus;

    @ManyToMany(cascade = CascadeType.PERSIST)
//    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinTable(name = "system_role_permission",joinColumns = {@JoinColumn(name = "role_id")},inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private Set<Permission> permissions;


//    @Transient
//    private Set<Menu> menus;


//    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
//    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    private Set<Permission> permissions = new HashSet<>();
//    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
//    @JoinTable(name = "role_menu", joinColumns = @JoinColumn(columnDefinition = "role_id"), inverseJoinColumns = @JoinColumn(columnDefinition = "menu_id"))
//    private Set<Menu> menus = new HashSet<>();
}
