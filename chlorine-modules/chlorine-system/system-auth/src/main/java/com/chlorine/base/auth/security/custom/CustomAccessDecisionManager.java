package com.chlorine.base.auth.security.custom;

import com.chlorine.base.auth.security.filter.JwtAuthenticationTokenFilter;
import com.chlorine.base.auth.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import java.util.Collection;

/** 动态权限配置
 * @author chenlong
 * @date 2020/12/14
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDecisionManager.class);

    @Autowired
    PermissionService permissionService;
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        if (((FilterInvocation) o).getRequest().getMethod().equals(HttpMethod.OPTIONS.name())) {
            // 对于OPTIONS请求直接放行
            return;
        }

        // 获取请求url
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        // 从url获取需要的权限， 如果包含问号就去掉
        String path = requestUrl.substring(1, !requestUrl.contains("?") ? requestUrl.length() : requestUrl.indexOf("?"));

        if (!permissionService.isExist(path)) {
            return;
        }
        // 获取用户的权限列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean havePermission = authorities.stream()
                .anyMatch(authority ->
                            path.equals(authority.getAuthority()));

        if (havePermission) {
            // 放行
            return;
        }
        throw new AccessDeniedException("权限不足，无法访问");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
