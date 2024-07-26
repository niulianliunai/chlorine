package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.mvc.controller.BaseController;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.service.UserService;
import com.chlorine.base.mvc.service.BaseService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
*
* @author chenlong
* @date 2020/12/8
*/
@Api(tags = "UserController", value = "用户")
@RestController
@RequestMapping("User")
public class UserController extends BaseController<User> {
    @Autowired
    private UserService userService ;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private JPAQueryFactory queryFactory;

    public UserController(BaseService<User> service) {
        super(service);
    }

    @GetMapping("list")
    public CommonResult list() {
        return CommonResult.success(userService.list());
    }
    @PostMapping("login")
    public CommonResult login(@RequestBody User userParam) {
        String token = userService.login(userParam.getUsername(), userParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        return CommonResult.success(tokenMap);
    }
    @GetMapping("permission")
    public CommonResult<Set<Permission>> getPermissionList(HttpServletRequest request) {
        Long userId = userService.getUserIdFromRequest(request);
        Set<Permission> permissions = userService.listPermission(userId);
        return CommonResult.success(permissions);
    }

    @GetMapping("info")
    public CommonResult<UserDTO> getUserInfo(String token) {
        UserDTO user = userService.getUserByToken(token);
        user.setPassword(null);
        return CommonResult.success(user);
    }
    @PostMapping("/logout")
    public CommonResult logout() {
        return CommonResult.success(null);
    }
}
