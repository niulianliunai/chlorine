package com.chlorine.base.auth.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    @OneToMany(mappedBy = "role")
    @JsonIgnoreProperties({"role"})
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "role")
    @JsonIgnoreProperties({"role"})
    private Set<RolePermission> rolePermissions ;
//    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
//    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    private Set<Permission> permissions = new HashSet<>();
//    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
//    @JoinTable(name = "role_menu", joinColumns = @JoinColumn(columnDefinition = "role_id"), inverseJoinColumns = @JoinColumn(columnDefinition = "menu_id"))
//    private Set<Menu> menus = new HashSet<>();
}
