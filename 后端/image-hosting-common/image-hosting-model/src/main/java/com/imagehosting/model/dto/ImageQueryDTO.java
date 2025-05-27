package com.imagehosting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageQueryDTO {
    
    /**
     * 当前页码
     */
    @Builder.Default
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    @Builder.Default
    private Integer size = 12;
    
    /**
     * 关键字
     */
    private String keyword;
    
    /**
     * 排序字段
     */
    @Builder.Default
    private String sortField = "create_time";
    
    /**
     * 排序方式（asc/desc）
     */
    @Builder.Default
    private String sortOrder = "desc";
} 