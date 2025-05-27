package com.imagehosting.service;

import com.imagehosting.model.dto.ImageProcessDTO;

/**
 * 图片处理服务接口
 */
public interface ImageProcessService {

    /**
     * 处理图片
     *
     * @param processDTO 处理参数
     * @return 处理后的图片URL
     */
    String processImage(ImageProcessDTO processDTO);
    
    /**
     * 调整图片大小
     *
     * @param imageId 图片ID
     * @param width   宽度
     * @param height  高度
     * @return 处理后的图片URL
     */
    String resizeImage(Long imageId, Integer width, Integer height);
    
    /**
     * 裁剪图片
     *
     * @param imageId 图片ID
     * @param width   宽度
     * @param height  高度
     * @return 处理后的图片URL
     */
    String cropImage(Long imageId, Integer width, Integer height);
    
    /**
     * 添加水印
     *
     * @param imageId   图片ID
     * @param text      水印文字
     * @param position  水印位置
     * @return 处理后的图片URL
     */
    String addWatermark(Long imageId, String text, String position);
    
    /**
     * 压缩图片
     *
     * @param imageId 图片ID
     * @param quality 质量（1-100）
     * @return 处理后的图片URL
     */
    String compressImage(Long imageId, Integer quality);
    
    /**
     * 转换图片格式
     *
     * @param imageId 图片ID
     * @param format  目标格式
     * @return 处理后的图片URL
     */
    String convertFormat(Long imageId, String format);
} 