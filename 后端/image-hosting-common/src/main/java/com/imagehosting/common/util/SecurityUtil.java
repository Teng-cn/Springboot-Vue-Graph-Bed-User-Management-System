package com.imagehosting.common.util;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
@Slf4j
public class SecurityUtil {

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.error("获取用户ID失败: 认证对象为null");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }
        
        if (!authentication.isAuthenticated()) {
            log.error("获取用户ID失败: 用户未认证");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        log.debug("认证主体类型: {}, 值: {}", principal != null ? principal.getClass().getName() : "null", principal);
        
        if (principal instanceof Long) {
            Long userId = (Long) principal;
            log.debug("成功获取用户ID: {}", userId);
            return userId;
        } else if (principal instanceof String && ((String) principal).matches("\\d+")) {
            // 尝试将字符串转换为Long
            try {
                Long userId = Long.parseLong((String) principal);
                log.debug("成功从字符串转换用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.error("将字符串转换为用户ID失败", e);
            }
        }
        
        log.error("获取用户ID失败: 认证主体不是Long类型: {}", principal);
        throw new BusinessException(ResultCode.UNAUTHORIZED, "获取用户ID失败");
    }
    
    /**
     * 获取当前认证对象
     *
     * @return 认证对象
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("获取认证对象失败: 认证对象为null");
        } else {
            log.debug("成功获取认证对象: {}", authentication);
        }
        return authentication;
    }
    
    /**
     * 判断当前用户是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        boolean authenticated = authentication != null && authentication.isAuthenticated();
        log.debug("用户认证状态: {}", authenticated);
        return authenticated;
    }
} 