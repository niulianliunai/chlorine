package com.chlorine.base.auth.service;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.repository.PermissionRepository;
import com.chlorine.base.auth.repository.RoleRepository;
import com.chlorine.base.auth.repository.UserRepository;
import com.chlorine.redis.config.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRedisService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.user}")
    private String REDIS_KEY_USER;
    @Value("${redis.key.permissionList}")
    private String REDIS_KEY_PERMISSION_LIST;

    public void delUser(Long userId) {
        User user = userRepository.getOne(userId);
        if (user.getUsername() != null) {
            String key = REDIS_DATABASE + ":" + REDIS_KEY_USER + ":" + user.getUsername();
            redisUtil.del(key);
        }
    }

    public void delPermissionList(Long userId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_LIST + ":" + userId;
        redisUtil.del(key);
    }

    public void delPermissionListByRole(Long roleId) {
        Role role = roleRepository.getOne(roleId);
//        Set<Permission> permissionList = role.getPermissions();

//        if (!permissionList.isEmpty()) {
//            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_LIST + ":";
//            List<String> keys = permissionList.stream().map(permission -> keyPrefix + permission.getId()).collect(Collectors.toList());
//            redisUtil.del(keys);
//        }
    }

    public void delPermissionListByRoleIds(List<Long> roleIds) {
        List<Role> roleList = roleRepository.findAllById(roleIds);
        roleList.forEach(role -> delPermissionListByRole(role.getId()));
    }

    public void delPermissionListByPermission(Long permissionId) {
        List<Long> userIdList = new ArrayList<>();

//        permissionRepository.getOne(permissionId).getRoles().stream().map(Role::getUsers).forEach(users -> {
//            users.forEach(user -> {
//                userIdList.add(user.getId());
//            });
//        });
        if (!userIdList.isEmpty()) {
            String keyPrefix = REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_LIST + ":";
            List<String> keys = userIdList.stream().map(userId -> keyPrefix + userId).collect(Collectors.toList());
            redisUtil.del(keys);
        }
    }

    public UserDTO getUser(String username) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_USER + ":" + username;
        return (UserDTO) redisUtil.get(key);
    }

    public void setUser(UserDTO user) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_USER + ":" + user.getUsername();
        redisUtil.set(key, user, REDIS_EXPIRE);
    }

    public List<Permission> getPermissionList(String  username) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_LIST + ":" + username;
        return (List<Permission>) redisUtil.get(key);
    }

    public void setPermissionList(String  username, List<Permission> permissionList) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_PERMISSION_LIST + ":" + username;
        redisUtil.set(key, permissionList, REDIS_EXPIRE);
    }
}
