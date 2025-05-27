package com.imagehosting.model.vo;

import com.imagehosting.model.dto.ImageStatDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员统计VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsVO {
    
    /**
     * 总用户数
     */
    private Long totalUsers;
    
    /**
     * 今日新增用户数
     */
    private Long newUsersToday;
    
    /**
     * 总图片数
     */
    private Long totalImages;
    
    /**
     * 今日上传图片数
     */
    private Long uploadedToday;
    
    /**
     * 总存储空间(字节)
     */
    private Long totalStorage;
    
    /**
     * 7天图片上传统计
     */
    private List<ImageStatDTO> weekStats;
    
    /**
     * 30天图片上传统计
     */
    private List<ImageStatDTO> monthStats;
} 