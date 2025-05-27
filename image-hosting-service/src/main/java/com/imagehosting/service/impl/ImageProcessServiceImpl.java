package com.imagehosting.service.impl;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import com.imagehosting.common.util.SecurityUtil;
import com.imagehosting.dao.ImageDao;
import com.imagehosting.model.dto.ImageProcessDTO;
import com.imagehosting.model.entity.Image;
import com.imagehosting.service.ImageProcessService;
import com.imagehosting.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 图片处理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessServiceImpl implements ImageProcessService {

    private final ImageDao imageDao;
    private final StorageService storageService;

    @Value("${storage.local.path}")
    private String storagePath;

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    @Override
    @Cacheable(value = "processedImages", key = "#processDTO.toString()", unless = "#result == null")
    public String processImage(ImageProcessDTO processDTO) {
        if (processDTO == null || processDTO.getImageId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "图片ID不能为空");
        }

        // 查询图片
        Image image = getAndVerifyImage(processDTO.getImageId());

        // 根据处理类型选择不同的处理方法
        String processType = processDTO.getProcessType();
        if (!StringUtils.hasText(processType)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "处理类型不能为空");
        }

        switch (processType.toLowerCase()) {
            case "resize":
                return resizeImage(processDTO.getImageId(), processDTO.getWidth(), processDTO.getHeight());
            case "crop":
                return cropImage(processDTO.getImageId(), processDTO.getWidth(), processDTO.getHeight());
            case "watermark":
                return addWatermark(processDTO.getImageId(), processDTO.getWatermarkText(), processDTO.getWatermarkPosition());
            case "compress":
                return compressImage(processDTO.getImageId(), processDTO.getQuality());
            case "format":
                return convertFormat(processDTO.getImageId(), processDTO.getFormat());
            default:
                throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的处理类型: " + processType);
        }
    }

    @Override
    @Cacheable(value = "resizedImages", key = "#imageId + '_' + #width + '_' + #height", unless = "#result == null")
    public String resizeImage(Long imageId, Integer width, Integer height) {
        // 参数校验
        if (width == null && height == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "宽度和高度不能同时为空");
        }

        // 查询图片
        Image image = getAndVerifyImage(imageId);

        try {
            // 获取源文件
            File sourceFile = new File(storagePath, image.getPath());
            if (!sourceFile.exists()) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源文件不存在");
            }

            // 获取文件扩展名
            String extension = getExtension(image.getName());

            // 创建目标文件名
            String targetFileName = generateProcessedFileName(image.getName(), "resize_" + width + "x" + height);
            File targetFile = createTargetFile("resize", targetFileName);

            // 调整大小
            if (width != null && height != null) {
                Thumbnails.of(sourceFile)
                        .size(width, height)
                        .toFile(targetFile);
            } else if (width != null) {
                BufferedImage bufferedImage = ImageIO.read(sourceFile);
                double ratio = (double) width / bufferedImage.getWidth();
                int calculatedHeight = (int) (bufferedImage.getHeight() * ratio);
                Thumbnails.of(sourceFile)
                        .size(width, calculatedHeight)
                        .toFile(targetFile);
            } else {
                BufferedImage bufferedImage = ImageIO.read(sourceFile);
                double ratio = (double) height / bufferedImage.getHeight();
                int calculatedWidth = (int) (bufferedImage.getWidth() * ratio);
                Thumbnails.of(sourceFile)
                        .size(calculatedWidth, height)
                        .toFile(targetFile);
            }

            // 返回相对路径
            return storageService.getFileUrl(getRelativePath(targetFile));
        } catch (IOException e) {
            log.error("调整图片大小失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "调整图片大小失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "croppedImages", key = "#imageId + '_' + #width + '_' + #height", unless = "#result == null")
    public String cropImage(Long imageId, Integer width, Integer height) {
        // 参数校验
        if (width == null || height == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "宽度和高度不能为空");
        }

        // 查询图片
        Image image = getAndVerifyImage(imageId);

        try {
            // 获取源文件
            File sourceFile = new File(storagePath, image.getPath());
            if (!sourceFile.exists()) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源文件不存在");
            }

            // 获取文件扩展名
            String extension = getExtension(image.getName());

            // 创建目标文件名
            String targetFileName = generateProcessedFileName(image.getName(), "crop_" + width + "x" + height);
            File targetFile = createTargetFile("crop", targetFileName);

            // 裁剪图片（从中心裁剪）
            Thumbnails.of(sourceFile)
                    .sourceRegion(Positions.CENTER, width, height)
                    .size(width, height)
                    .toFile(targetFile);

            // 返回相对路径
            return storageService.getFileUrl(getRelativePath(targetFile));
        } catch (IOException e) {
            log.error("裁剪图片失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "裁剪图片失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "watermarkedImages", key = "#imageId + '_' + #text + '_' + #position", unless = "#result == null")
    public String addWatermark(Long imageId, String text, String position) {
        // 参数校验
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "水印文字不能为空");
        }

        // 查询图片
        Image image = getAndVerifyImage(imageId);

        try {
            // 获取源文件
            File sourceFile = new File(storagePath, image.getPath());
            if (!sourceFile.exists()) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源文件不存在");
            }

            // 获取文件扩展名
            String extension = getExtension(image.getName());

            // 创建目标文件名
            String targetFileName = generateProcessedFileName(image.getName(), "watermark");
            File targetFile = createTargetFile("watermark", targetFileName);

            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(sourceFile);

            // 创建Graphics2D对象
            BufferedImage watermarkedImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = watermarkedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);

            // 设置水印属性
            g2d.setColor(new Color(0, 0, 0, 60)); // 半透明黑色
            g2d.setFont(new Font("Arial", Font.BOLD, 36));

            // 计算水印位置
            int x = 20;
            int y = originalImage.getHeight() - 40;

            if (StringUtils.hasText(position)) {
                switch (position.toLowerCase()) {
                    case "topleft":
                        x = 20;
                        y = 50;
                        break;
                    case "topright":
                        x = originalImage.getWidth() - 20 - g2d.getFontMetrics().stringWidth(text);
                        y = 50;
                        break;
                    case "bottomleft":
                        x = 20;
                        y = originalImage.getHeight() - 40;
                        break;
                    case "bottomright":
                        x = originalImage.getWidth() - 20 - g2d.getFontMetrics().stringWidth(text);
                        y = originalImage.getHeight() - 40;
                        break;
                    case "center":
                        x = (originalImage.getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2;
                        y = originalImage.getHeight() / 2;
                        break;
                }
            }

            // 绘制水印
            g2d.drawString(text, x, y);
            g2d.dispose();

            // 保存图片
            ImageIO.write(watermarkedImage, extension, targetFile);

            // 返回相对路径
            return storageService.getFileUrl(getRelativePath(targetFile));
        } catch (IOException e) {
            log.error("添加水印失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "添加水印失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "compressedImages", key = "#imageId + '_' + #quality", unless = "#result == null")
    public String compressImage(Long imageId, Integer quality) {
        // 参数校验
        if (quality == null || quality < 1 || quality > 100) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "压缩质量应该在1-100之间");
        }

        // 查询图片
        Image image = getAndVerifyImage(imageId);

        try {
            // 获取源文件
            File sourceFile = new File(storagePath, image.getPath());
            if (!sourceFile.exists()) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源文件不存在");
            }

            // 创建目标文件名
            String targetFileName = generateProcessedFileName(image.getName(), "compress_" + quality);
            File targetFile = createTargetFile("compress", targetFileName);

            // 压缩图片
            float qualityFloat = quality / 100.0f;
            Thumbnails.of(sourceFile)
                    .scale(1.0)
                    .outputQuality(qualityFloat)
                    .toFile(targetFile);

            // 返回相对路径
            return storageService.getFileUrl(getRelativePath(targetFile));
        } catch (IOException e) {
            log.error("压缩图片失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "压缩图片失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "convertedImages", key = "#imageId + '_' + #format", unless = "#result == null")
    public String convertFormat(Long imageId, String format) {
        // 参数校验
        if (!StringUtils.hasText(format)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "目标格式不能为空");
        }

        String formatLower = format.toLowerCase();
        if (!SUPPORTED_FORMATS.contains(formatLower)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的格式: " + format);
        }

        // 查询图片
        Image image = getAndVerifyImage(imageId);

        try {
            // 获取源文件
            File sourceFile = new File(storagePath, image.getPath());
            if (!sourceFile.exists()) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "源文件不存在");
            }

            // 获取文件名（不含扩展名）
            String nameWithoutExtension = getNameWithoutExtension(image.getName());

            // 创建目标文件名
            String targetFileName = nameWithoutExtension + "." + formatLower;
            File targetFile = createTargetFile("format", targetFileName);

            // 转换格式
            Thumbnails.of(sourceFile)
                    .scale(1.0)
                    .outputFormat(formatLower)
                    .toFile(targetFile);

            // 返回相对路径
            return storageService.getFileUrl(getRelativePath(targetFile));
        } catch (IOException e) {
            log.error("转换格式失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "转换格式失败: " + e.getMessage());
        }
    }

    /**
     * 获取并验证图片
     *
     * @param imageId 图片ID
     * @return 图片实体
     */
    private Image getAndVerifyImage(Long imageId) {
        // 查询图片
        Image image = imageDao.findById(imageId);
        if (image == null || image.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "图片不存在");
        }

        // 验证图片所有权
        Long userId = SecurityUtil.getCurrentUserId();
        if (!image.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该图片");
        }

        return image;
    }

    /**
     * 创建目标文件
     *
     * @param processType 处理类型
     * @param fileName    文件名
     * @return 目标文件
     * @throws IOException IO异常
     */
    private File createTargetFile(String processType, String fileName) throws IOException {
        // 生成日期目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 完整目录路径
        String fullDirectory = storagePath + File.separator + "processed" + File.separator + processType + File.separator + datePath;
        
        // 确保目录存在
        Path directoryPath = Paths.get(fullDirectory);
        Files.createDirectories(directoryPath);
        
        // 完整文件路径
        return new File(fullDirectory, fileName);
    }

    /**
     * 生成处理后的文件名
     *
     * @param originalName 原始文件名
     * @param suffix       后缀
     * @return 处理后的文件名
     */
    private String generateProcessedFileName(String originalName, String suffix) {
        String extension = getExtension(originalName);
        String nameWithoutExt = getNameWithoutExtension(originalName);
        return nameWithoutExt + "_" + suffix + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 获取不含扩展名的文件名
     *
     * @param fileName 文件名
     * @return 不含扩展名的文件名
     */
    private String getNameWithoutExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 获取相对路径
     *
     * @param file 文件
     * @return 相对路径
     */
    private String getRelativePath(File file) {
        String absolutePath = file.getAbsolutePath();
        return absolutePath.substring(storagePath.length() + 1).replace("\\", "/");
    }
} 