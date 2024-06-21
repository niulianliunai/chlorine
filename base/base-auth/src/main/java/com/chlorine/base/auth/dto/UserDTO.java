package com.chlorine.base.auth.dto;

import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDTO  {

    private Long id;
    private String username;

    private String password;

    private String name;

    private String avatar;

    private List<String> roles;

    public UserDTO() {

    }

    public UserDTO(Long id, String username, String password, String name, String avatar, List<String> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.roles = roles;
    }

    public UserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        roles = user.getUserRoles().stream().map(item->item.getRole().getName()).collect(Collectors.toList());
    }
}
