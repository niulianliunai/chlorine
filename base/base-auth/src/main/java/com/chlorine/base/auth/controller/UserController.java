package com.chlorine.base.auth.controller;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.service.UserService;
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
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService ;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping("page")
    public CommonResult page(int pageNumber,int pageSize,String contain) {
        return userService.page(pageNumber, pageSize,contain) ;
    }
    @PostMapping("save")
    public CommonResult save(User user) {
        return userService.save(user);
    }
    @GetMapping("list")
    public CommonResult list() {
        return userService.list();
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

    @ApiOperation("test")
    @GetMapping("test")
    public CommonResult test() {
        return CommonResult.success(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @ApiOperation("test1")
    @GetMapping("test1")
    public CommonResult test1() {
        return CommonResult.success(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
