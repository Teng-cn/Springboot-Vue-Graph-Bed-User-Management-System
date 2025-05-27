package com.imagehosting.web.security;

import com.imagehosting.common.util.JwtUtil;
import com.imagehosting.dao.UserDao;
import com.imagehosting.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT授权过滤器
 */
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDao userDao;
    
    /**
     * 构造方法
     */
    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDao userDao) {
        this.jwtUtil = jwtUtil;
        this.userDao = userDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.debug("处理请求: {}, 方法: {}", requestURI, request.getMethod());
        
        // 获取请求头中的token
        String token = getTokenFromRequest(request);
        
        // 如果token不为空且有效，则设置认证信息
        if (StringUtils.hasText(token)) {
            log.debug("找到token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            
            if (jwtUtil.validateToken(token)) {
                // 从token中获取用户ID
                String userId = jwtUtil.getUserIdFromToken(token);
                log.debug("token有效，用户ID: {}", userId);
                
                try {
                    // 查询用户信息
                    User user = userDao.findById(Long.valueOf(userId));
                    if (user != null) {
                        log.debug("找到用户: id={}, username={}, roleType={}", user.getId(), user.getUsername(), user.getRoleType());
                        
                        // 创建权限列表
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        
                        // 根据用户角色类型添加权限
                        if (user.getRoleType() == 1) {
                            // 管理员角色
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                            log.debug("用户 {} 具有管理员权限 ROLE_ADMIN", userId);
                        } else {
                            // 普通用户角色
                            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                            log.debug("用户 {} 具有普通用户权限 ROLE_USER", userId);
                        }
                        
                        // 创建认证令牌，包含用户ID和权限
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                Long.valueOf(userId), null, authorities);
                        
                        // 设置认证信息
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("设置认证信息成功，用户ID: {}, 权限: {}", userId, authorities);
                        
                        // 打印当前认证信息以进行调试
                        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                        if (currentAuth != null) {
                            log.debug("当前认证信息: 主体={}, 权限={}, 已认证={}", 
                                currentAuth.getPrincipal(), 
                                currentAuth.getAuthorities(), 
                                currentAuth.isAuthenticated());
                        } else {
                            log.warn("设置认证信息后，获取当前认证信息为null");
                        }
                    } else {
                        log.warn("未找到ID为 {} 的用户", userId);
                    }
                } catch (Exception e) {
                    log.error("处理用户权限时发生错误: {}", e.getMessage(), e);
                }
            } else {
                log.warn("无效的token");
            }
        } else {
            log.debug("请求中没有token");
        }
        
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头中获取token
        String bearerToken = request.getHeader(jwtUtil.getHeader());
        log.debug("请求头中的Authorization: {}", bearerToken);
        
        // 如果token不为空且以指定前缀开头，则返回token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtUtil.getTokenPrefix())) {
            String token = bearerToken.substring(jwtUtil.getTokenPrefix().length());
            log.debug("提取的token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            return token;
        }
        
        log.debug("未找到有效的token");
        return null;
    }
} 