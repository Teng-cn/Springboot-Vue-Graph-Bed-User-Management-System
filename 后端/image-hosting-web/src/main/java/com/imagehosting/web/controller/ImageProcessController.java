package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import com.imagehosting.model.dto.ImageProcessDTO;
import com.imagehosting.service.ImageProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 图片处理控制器
 */
@Tag(name = "图片处理接口")
@RestController
@RequestMapping("/image/process")
@RequiredArgsConstructor
public class ImageProcessController {

    private final ImageProcessService imageProcessService;

    /**
     * 处理图片
     *
     * @param processDTO 处理参数
     * @return 处理结果
     */
    @Operation(summary = "处理图片")
    @PostMapping
    public Result<String> processImage(@RequestBody ImageProcessDTO processDTO) {
        return Result.success(imageProcessService.processImage(processDTO));
    }

    /**
     * 调整图片大小
     *
     * @param imageId 图片ID
     * @param width   宽度
     * @param height  高度
     * @return 处理结果
     */
    @Operation(summary = "调整图片大小")
    @GetMapping("/resize")
    public Result<String> resizeImage(
            @RequestParam("imageId") Long imageId,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height) {
        return Result.success(imageProcessService.resizeImage(imageId, width, height));
    }

    /**
     * 裁剪图片
     *
     * @param imageId 图片ID
     * @param width   宽度
     * @param height  高度
     * @return 处理结果
     */
    @Operation(summary = "裁剪图片")
    @GetMapping("/crop")
    public Result<String> cropImage(
            @RequestParam("imageId") Long imageId,
            @RequestParam("width") Integer width,
            @RequestParam("height") Integer height) {
        return Result.success(imageProcessService.cropImage(imageId, width, height));
    }

    /**
     * 添加水印
     *
     * @param imageId   图片ID
     * @param text      水印文字
     * @param position  水印位置
     * @return 处理结果
     */
    @Operation(summary = "添加水印")
    @GetMapping("/watermark")
    public Result<String> addWatermark(
            @RequestParam("imageId") Long imageId,
            @RequestParam("text") String text,
            @RequestParam(value = "position", defaultValue = "bottomright") String position) {
        return Result.success(imageProcessService.addWatermark(imageId, text, position));
    }

    /**
     * 压缩图片
     *
     * @param imageId 图片ID
     * @param quality 质量（1-100）
     * @return 处理结果
     */
    @Operation(summary = "压缩图片")
    @GetMapping("/compress")
    public Result<String> compressImage(
            @RequestParam("imageId") Long imageId,
            @RequestParam(value = "quality", defaultValue = "80") Integer quality) {
        return Result.success(imageProcessService.compressImage(imageId, quality));
    }

    /**
     * 转换图片格式
     *
     * @param imageId 图片ID
     * @param format  目标格式
     * @return 处理结果
     */
    @Operation(summary = "转换图片格式")
    @GetMapping("/format")
    public Result<String> convertFormat(
            @RequestParam("imageId") Long imageId,
            @RequestParam("format") String format) {
        return Result.success(imageProcessService.convertFormat(imageId, format));
    }
} 