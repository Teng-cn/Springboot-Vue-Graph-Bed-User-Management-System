package com.imagehosting.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsVO {
    
    /**
     * 图片总数
     */
    private Long totalImages;
    
    /**
     * 总存储空间（字节）
     */
    private Long totalStorage;
    
    /**
     * 总访问次数
     */
    private Long totalAccess;
    
    /**
     * 今日上传数
     */
    private Long todayUploaded;
} 