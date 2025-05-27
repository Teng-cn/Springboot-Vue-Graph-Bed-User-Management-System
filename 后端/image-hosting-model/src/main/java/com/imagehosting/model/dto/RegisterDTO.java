package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 注册DTO
 */
@Data
public class RegisterDTO {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
} 