package com.chlorine.base.auth.dto;

import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.entity.User;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDTO {

    private Long id;
    private String username;

    private String password;

    private String name;

    private String avatar;

    private Set<String> roles;

    private Set<String> permissions;

    public UserDTO() {

    }

    public UserDTO(Long id, String username, String password, String name, String avatar, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.roles = roles;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        Set<Role> userRoles = user.getRoles();
        this.roles = userRoles.stream().map(Role::getName).collect(Collectors.toSet());
        this.permissions = new HashSet<>();
        userRoles.forEach(role -> permissions.addAll(role.getPermissions().stream().map(Permission::getPath).collect(Collectors.toSet())));
    }
}
