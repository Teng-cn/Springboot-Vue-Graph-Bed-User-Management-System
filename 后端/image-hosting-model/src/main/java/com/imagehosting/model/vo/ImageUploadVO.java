package com.imagehosting.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片上传结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadVO {
    
    /**
     * 图片ID
     */
    private Long id;
    
    /**
     * 图片名称
     */
    private String name;
    
    /**
     * 访问URL
     */
    private String url;
    
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
} 