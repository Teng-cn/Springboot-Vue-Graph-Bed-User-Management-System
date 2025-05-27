package com.imagehosting.web.controller;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.Result;
import com.imagehosting.model.dto.ImageQueryDTO;
import com.imagehosting.model.vo.ImageUploadVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import com.imagehosting.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片控制器
 */
@Tag(name = "图片管理接口")
@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 上传结果
     */
    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<ImageUploadVO> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 打印请求头信息
            String authHeader = request.getHeader("Authorization");
            log.debug("上传图片请求，Authorization: {}", authHeader);
            
            // 打印当前认证状态
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.debug("当前认证用户: {}, 类型: {}", 
                        authentication.getPrincipal(), 
                        authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getName() : "null");
            } else {
                log.warn("当前请求没有认证信息");
            }
            
            // 调用服务上传图片
            return Result.success(imageService.uploadImage(file));
        } catch (BusinessException e) {
            log.error("上传图片业务异常: {}", e.getMessage());
            return Result.failed(e.getResultCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("上传图片系统异常", e);
            return Result.failed("上传图片失败：" + e.getMessage());
        }
    }

    /**
     * 获取图片列表
     *
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键字
     * @return 图片列表
     */
    @Operation(summary = "获取图片列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        
        // 创建查询对象
        ImageQueryDTO queryDTO = ImageQueryDTO.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .build();
        
        // 调用服务获取数据
        PageVO<ImageVO> pageData = imageService.getImageList(queryDTO);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageData.getList());
        result.put("total", pageData.getTotal());
        result.put("page", pageData.getPage());
        result.put("size", pageData.getSize());
        
        return Result.success(result);
    }

    /**
     * 获取图片详情
     *
     * @param id 图片ID
     * @return 图片详情
     */
    @Operation(summary = "获取图片详情")
    @GetMapping("/{id}")
    public Result<ImageVO> getImageById(@PathVariable("id") Long id) {
        return Result.success(imageService.getImageById(id));
    }

    /**
     * 删除图片
     *
     * @param id 图片ID
     * @return 删除结果
     */
    @Operation(summary = "删除图片")
    @DeleteMapping("/{id}")
    public Result<Void> deleteImage(@PathVariable("id") Long id) {
        imageService.deleteImage(id);
        return Result.success();
    }

    /**
     * 访问图片
     *
     * @param id 图片ID
     * @return 图片URL
     */
    @Operation(summary = "访问图片")
    @GetMapping("/access/{id}")
    public Result<String> accessImage(@PathVariable("id") Long id) {
        return Result.success(imageService.accessImage(id));
    }
} 