package com.chlorine.base.auth.entity;

import com.chlorine.base.mvc.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
*
* @author chenlong
* @date 2020/12/17
*/
@Getter
@Setter
@Entity
@Table(name = "system_user")
@Where(clause = "deleted<>1")
public class User extends BaseEntity {

	private String username;

	private String password;

	private String name;

	private String avatar;

	@ManyToMany(cascade = {CascadeType.PERSIST})
	@JoinTable(name = "system_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
	private Set<Role> roles;
}

