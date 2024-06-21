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
@Table(name = "user_role")
@Where(clause = "deleted<>1")
public class UserRole extends RelationBaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"userRoles"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnoreProperties({"userRoles"})
    private Role role;
}
