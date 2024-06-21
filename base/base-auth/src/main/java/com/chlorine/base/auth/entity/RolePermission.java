package com.chlorine.base.auth.entity;

import com.chlorine.base.mvc.entity.RelationBaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "role_permission")
@Where(clause = "deleted<>1")
public class RolePermission extends RelationBaseEntity {
    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"rolePermissions"})
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    @JsonIgnoreProperties({"rolePermissions"})
    private Permission permission;
}
