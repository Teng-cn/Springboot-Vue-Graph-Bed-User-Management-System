package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 图片处理DTO
 */
@Data
public class ImageProcessDTO {

    /**
     * 图片ID
     */
    private Long imageId;
    
    /**
     * 处理类型：resize、crop、watermark、compress
     */
    private String processType;
    
    /**
     * 宽度
     */
    private Integer width;
    
    /**
     * 高度
     */
    private Integer height;
    
    /**
     * 水印文字
     */
    private String watermarkText;
    
    /**
     * 水印位置：topLeft, topRight, bottomLeft, bottomRight, center
     */
    private String watermarkPosition;
    
    /**
     * 压缩质量（1-100）
     */
    private Integer quality;
    
    /**
     * 输出格式：jpg, png, webp, gif
     */
    private String format;
} 