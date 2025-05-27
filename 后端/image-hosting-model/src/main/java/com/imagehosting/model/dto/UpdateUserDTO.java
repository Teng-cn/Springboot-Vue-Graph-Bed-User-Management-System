package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 更新用户信息DTO
 */
@Data
public class UpdateUserDTO {
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
} 