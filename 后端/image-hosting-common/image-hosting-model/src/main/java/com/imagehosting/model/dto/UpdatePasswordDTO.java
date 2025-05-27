package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 修改密码DTO
 */
@Data
public class UpdatePasswordDTO {
    
    /**
     * 旧密码
     */
    private String oldPassword;
    
    /**
     * 新密码
     */
    private String newPassword;
} 