package com.imagehosting.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图片实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    
    /**
     * 图片ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 图片名称
     */
    private String name;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 存储路径
     */
    private String path;
    
    /**
     * 访问URL
     */
    private String url;
    
    /**
     * MD5值
     */
    private String md5;
    
    /**
     * 图片大小（字节）
     */
    private Long size;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    /**
     * 媒体类型
     */
    private String mimeType;
    
    /**
     * 访问次数
     */
    private Long accessCount;
    
    /**
     * 删除标志（0未删除，1已删除）
     */
    private Integer deleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 