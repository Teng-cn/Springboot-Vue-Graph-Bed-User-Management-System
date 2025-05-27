package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 管理员更新用户DTO
 */
@Data
public class AdminUserUpdateDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
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
    
    /**
     * 角色类型（0普通用户，1管理员）
     */
    private Integer roleType;
    
    /**
     * 帐号状态（0正常，1禁用）
     */
    private Integer status;
} 