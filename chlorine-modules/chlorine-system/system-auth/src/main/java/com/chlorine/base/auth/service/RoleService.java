package com.chlorine.base.auth.service;

import com.chlorine.base.auth.entity.Menu;
import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.mvc.repository.BaseRepository;
import com.chlorine.base.mvc.service.BaseService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService extends BaseService<Role> {
    @Autowired
    MenuService menuService;
    @Autowired
    JPAQueryFactory queryFactory;

    protected RoleService(BaseRepository<Role> repository) {
        super(repository);
    }

    @Transactional
    public Role save(Role role) {
        Set<Menu> menus = role.getMenus();
        Set<Menu> saveMenus = new LinkedHashSet<>(menus);
        addChildren(menus, saveMenus);
        role.setMenus(saveMenus);
        return super.save(role);
    }

    public void addChildren(Set<Menu> menus, Set<Menu> saveMenus) {
        if (menus == null) {
            return;
        }
        menus.forEach(menu -> {
            if (menu.getChildren() != null) {
                saveMenus.addAll(menu.getChildren());
                addChildren(menu.getChildren(), saveMenus);
            }
        });
    }

    public Page<Role> page(Role role) {
        Page<Role> rolePage = super.page(role);
        rolePage.getContent().forEach(item -> {
            item.setMenus(menuService.listRoleMenu(item.getId()));
        });
        return rolePage;
    }

    public List<Role> list() {
        List<Role> roleList = super.list();
        for (Role role : roleList) {
            role.setMenus(menuService.listRoleMenu(role.getId()));
        }
        return roleList;
    }
}
