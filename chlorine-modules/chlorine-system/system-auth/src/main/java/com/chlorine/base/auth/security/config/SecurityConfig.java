package com.chlorine.base.auth.security.config;


import com.chlorine.base.auth.dto.UserDTO;
import com.chlorine.base.auth.security.custom.CustomAccessDecisionManager;
import com.chlorine.base.auth.security.custom.CustomAccessDeniedHandler;
import com.chlorine.base.auth.security.custom.CustomAuthenticationEntryPoint;
import com.chlorine.base.mvc.dto.CommonResult;
import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.entity.User;
import com.chlorine.base.auth.security.UserDetailsImpl;
import com.chlorine.base.auth.security.filter.JwtAuthenticationTokenFilter;
import com.chlorine.base.auth.service.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.transaction.Transactional;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chenlong
 * @date 2020/12/7
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    CustomAccessDecisionManager customAccessDecisionManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                // 禁用session （因为用jwt来鉴权和认证，不需要session）
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                // 其他所有请求都要鉴权
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl("user/logout")
                .logoutSuccessHandler((req, response, auth) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(CommonResult.success(auth).toString());
                    out.flush();
                    out.close();
                });

        // 禁用缓存
        http.headers().cacheControl();
        // 添加jwt过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加自定义未授权和未登录结果返回
        http.exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint);
        http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setAccessDecisionManager(customAccessDecisionManager);
                return o;
            }
        });
    }

    /**
     * 配置userDetailsService和passwordEncoder
     *
     * @param auth auth
     * @throws Exception e
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 取出用户权限
     *
     * @return userDetailService
     */
    @Override
    @Bean
    @Transactional
    public UserDetailsService userDetailsService() {
        return username -> {
            UserDTO userDTO = userService.getUserByUsername(username, true);
            if (userDTO != null) {
                return new UserDetailsImpl(userDTO);
            }
            throw new UsernameNotFoundException("用户名或密码错误");
        };
    }

    /**
     * 自定义拦截器
     *
     * @return jwtAuthenticationTokenFilter
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }
}
