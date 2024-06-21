package com.chlorine.base.auth.security;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
* security需要的用户详情
* @author chenlong
* @date 2020/12/8
*/
public class UserDetailsImpl implements UserDetails {
    private final UserDTO user;
    private final List<Permission> permissionList;
    public UserDetailsImpl(UserDTO user, List<Permission> permissionList) {
        this.user = user;
        this.permissionList = permissionList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissionList.stream()
                .filter(permission -> permission != null && permission.getPath() != null)
                .map(permission -> new SimpleGrantedAuthority(permission.getPath()))
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
