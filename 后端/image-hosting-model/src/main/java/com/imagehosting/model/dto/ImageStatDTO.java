package com.imagehosting.model.dto;

import lombok.Data;

/**
 * 图片统计DTO
 */
@Data
public class ImageStatDTO {

    /**
     * 日期
     */
    private String date;
    
    /**
     * 上传数量
     */
    private Integer count;
} 