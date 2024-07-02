package com.chlorine.base.auth.service;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.auth.entity.QPermission;
import com.chlorine.base.auth.entity.QRolePermission;
import com.chlorine.base.auth.entity.QUser;
import com.chlorine.base.auth.entity.QUserRole;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.repository.UserRepository;
import com.chlorine.base.auth.security.util.JwtTokenUtil;
import com.chlorine.base.mvc.util.EntityManagerUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author chenlong
 * @date 2020/12/8
 */
@Service
@Log4j2
public class UserService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRedisService userRedisService;
    @Autowired
    EntityManagerUtil entityManagerUtil;
    @Autowired
    JPAQueryFactory queryFactory;


    public CommonResult page(int pageNumber, int pageSize, String contain) {
        return CommonResult.success(userRepository.findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.hasLength(contain)) {
                predicateList.add(criteriaBuilder.like(root.get("name"), "%" + contain + "%"));
            }
            return criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()])).getRestriction();
        }, PageRequest.of(pageNumber - 1, pageSize)));
    }

    public CommonResult list() {
        return CommonResult.success(userRepository.findAll());
    }

    public UserDTO getUserByUsername(String username,boolean useCache) {
        if (useCache) {
            try{
                UserDTO userCache = userRedisService.getUser(username);
                if (userCache != null) {
                    return userCache;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("username"), username);
            }
        };
        Optional<User> user = userRepository.findOne(specification);
        log.info(user);
        return user.map(UserDTO::new).orElse(null);
    }
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    public String login(String username, String password) {

        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            token = JwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常：{}" + e.getMessage());
        }
        UserDTO user = getUserByUsername(username, false);
        userRedisService.setUser(user);
        return token;
    }

    public CommonResult save(User user) {

        Specification<User> specification =
                (Specification<User>) (root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("username"), user.getUsername());

        if (!userRepository.findAll(specification).isEmpty()) {
            return CommonResult.failed("用户名已存在");
        }

        if (user.getPassword() != null) {
            String encodePassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
        }
        return CommonResult.success(userRepository.save(user));
    }



    public Set<Permission> listPermission(Long userId) {
//        Set<Permission> permissions = new HashSet<>();
//        User userData =  entityManagerUtil.lazyLoad(User.class,user.getId(), "userRoles","role","rolePermissions");
//        if (userData != null) {
////            userData.getRoles().stream().map(UserRole::getRole).forEach(role -> permissions.addAll(role.getPermissions()));
//        }
        QPermission qPermission = QPermission.permission;
        QRolePermission qRolePermission = QRolePermission.rolePermission;
        QUser qUser = QUser.user;
        QUserRole qUserRole = QUserRole.userRole;
        List<Permission> permissionList = queryFactory.select(qPermission).from(qUser)
                .leftJoin(qUserRole).on(qUser.id.eq(qUserRole.user.id))
                .leftJoin(qRolePermission).on(qRolePermission.role.id.eq(qUserRole.role.id))
                .leftJoin(qPermission).on(qPermission.id.eq(qRolePermission.permission.id))
                .where(qUser.id.eq(userId))
                .fetch();
        return new HashSet<>(permissionList);
    }

    public Long getUserIdFromRequest(HttpServletRequest httpServletRequest) {
        // 获取http请求头中的token
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = JwtTokenUtil.getUserNameFromToken(authToken);
            Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
            List<User> users = userRepository.findAll(specification);
            if (users.size() > 0) {
                return users.get(0).getId();
            }
        }
        return null;
    }
    public User getUserFromRequest(HttpServletRequest httpServletRequest) {
        // 获取http请求头中的token
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = JwtTokenUtil.getUserNameFromToken(authToken);
            Specification<User> specification = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
            List<User> users = userRepository.findAll(specification);
            if (users.size() > 0) {
                return users.get(0);
            }
        }
        return null;
    }

    public UserDTO getUserByToken(String token) {
        String username = JwtTokenUtil.getUserNameFromToken(token);
        return getUserByUsername(username, true);
    }

}
