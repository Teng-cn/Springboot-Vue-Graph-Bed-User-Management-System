package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 用户查询DTO
 */
@Data
public class UserQueryDTO {
    
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 关键字
     */
    private String keyword;
    
    /**
     * 排序字段
     */
    private String sortField = "create_time";
    
    /**
     * 排序方式（asc/desc）
     */
    private String sortOrder = "desc";
    
    /**
     * 角色类型（0普通用户，1管理员）
     */
    private Integer roleType;
    
    /**
     * 状态（0正常，1禁用）
     */
    private Integer status;
} 