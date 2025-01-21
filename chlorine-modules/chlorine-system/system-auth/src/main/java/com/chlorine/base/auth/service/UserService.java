package com.chlorine.base.auth.service;

import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.repository.UserRepository;
import com.chlorine.base.auth.security.util.JwtTokenUtil;
import com.chlorine.base.mvc.repository.BaseRepository;
import com.chlorine.base.mvc.service.BaseService;
import com.chlorine.base.mvc.util.EntityManagerUtil;
import com.chlorine.base.util.UpdateUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.chlorine.base.auth.constant.QTableConstant.qUser;

/**
 * @author chenlong
 * @date 2020/12/8
 */
@Service
@Log4j2
public class UserService extends BaseService<User> {
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

    protected UserService(BaseRepository<User> repository) {
        super(repository);
    }

    @Override
    public List<User> list() {
        List<User> userList = super.list();
        userList.forEach(item -> {
            item.setPassword(null);
        });
        return userList;
    }

    @Override
    public Page<User> page(User user) {
        Page<User> page = super.page(user);
        page.getContent().forEach(item -> {
            item.setPassword(null);
        });
        return page;
    }

    public UserDTO getUserByUsername(String username, boolean useCache) {
        if (useCache) {
            try {
                UserDTO userCache = userRedisService.getUser(username);
                if (userCache != null) {
                    return userCache;
                }
            } catch (Exception e) {
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
        if (user.isPresent()) {
            return new UserDTO(user.get());
        }
        throw new RuntimeException("用户名不存在");
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
            LOGGER.warn("登录异常：{}" , e.getMessage());
        }
        UserDTO user = getUserByUsername(username, false);
        userRedisService.setUser(user);
        return token;
    }

    public User save(User user) {

        if (user.getPassword() != null) {
            String encodePassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
        }
        boolean exists = false;
        if (user.getId() != null) {
            exists = queryFactory.selectOne().from(qUser).where(qUser.username.eq(user.getUsername()).and(qUser.id.ne(user.getId()))).fetchCount() > 0;
        } else {
            exists = queryFactory.selectOne().from(qUser).where(qUser.username.eq(user.getUsername())).fetchCount() > 0;
        }
        if (exists) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getId() != null) {
            Optional<User> optionalEntity = repository.findById(user.getId());
            if (optionalEntity.isPresent()) {
                User target = optionalEntity.get();
                UpdateUtil.copyNullProperties(user, target);
                return repository.save(target);
            }
        }
        return repository.save(user);

    }


    public Set<Permission> listPermission(Long userId) {
//        Set<Permission> permissions = new HashSet<>();
//        User userData =  entityManagerUtil.lazyLoad(User.class,user.getId(), "userRoles","role","rolePermissions");
//        if (userData != null) {
////            userData.getRoles().stream().map(UserRole::getRole).forEach(role -> permissions.addAll(role.getPermissions()));
//        }
//        QPermission qPermission = QPermission.permission;
//        QUser qUser = QUser.user;
//        List<Permission> permissionList = queryFactory.select(qPermission).from(qUser)
//                .leftJoin(qUserRole).on(qUser.id.eq(qUserRole.user.id))
//                .leftJoin(qRolePermission).on(qRolePermission.role.id.eq(qUserRole.role.id))
//                .leftJoin(qPermission).on(qPermission.id.eq(qRolePermission.permission.id))
//                .where(qUser.id.eq(userId))
//                .fetch();
//        return new HashSet<>(permissionList);
        return new HashSet<>();
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
