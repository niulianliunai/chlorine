package com.chlorine.base.auth.security.filter;

import com.chlorine.base.auth.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* Jwt登录授权过滤器
* @author chenlong
* @date 2020/12/8
*/
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value(("${jwt.tokenHead}"))
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("JwtAuthenticationTokenFilter");
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);

        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            try {
                String authToken = authHeader.substring(this.tokenHead.length());
                String username = JwtTokenUtil.getUserNameFromToken(authToken);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    if (JwtTokenUtil.validateToken(authToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        LOGGER.info("Authenticated user: {}", username);
                    } else {
                        LOGGER.warn("Token validation failed for user: {}", username);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to process authentication token", ex);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
