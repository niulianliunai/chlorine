package com.chlorine.base.auth.service;

import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.entity.Menu;
import com.chlorine.base.auth.repository.MenuRepository;
import com.chlorine.base.mvc.service.TreeService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MenuService extends TreeService<Menu> {

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;

    protected MenuService(MenuRepository MenuRepository) {
        super(MenuRepository);
    }

    public Set<Menu> listRoleMenu(Long roleId) {
        Role role = roleService.findById(roleId).get();
        return listRoleMenu(role);
    }

    public Set<Menu> listRoleMenu(Role role) {
        Set<Menu> menus = role.getMenus();
        Set<Long> menuIds = menus.stream().map(Menu::getId).collect(Collectors.toSet());
        filterChildren(menus,menuIds);
        return menus.stream().filter(item -> item.getParent() == null).collect(Collectors.toSet());
    }
    public Set<Menu> listUserMenu(Long userId) {
        Set<Menu> menus = new TreeSet<>(Comparator.comparingInt(Menu::getSort));
        userService.findById(userId).get().getRoles().forEach(role -> menus.addAll(listRoleMenu(role)));
        return menus;
    }

    public Set<Permission> listUserPermissions(Long userId) {
        Set<Permission> permissions = new HashSet<>();
        return permissions;
    }

    public void filterChildren(Set<Menu> menus,Set<Long> menuIds) {
        if (menus.isEmpty()) {
            return;
        }
        menus.forEach(menu -> {
            menu.setChildren(menu.getChildren().stream().filter(child -> menuIds.contains(child.getId())).collect(Collectors.toSet()));
            filterChildren(menu.getChildren(),menuIds);
        });
    }
}
