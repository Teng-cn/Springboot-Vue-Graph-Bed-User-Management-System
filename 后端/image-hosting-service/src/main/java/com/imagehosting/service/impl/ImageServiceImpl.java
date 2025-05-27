package com.imagehosting.service.impl;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import com.imagehosting.common.util.SecurityUtil;
import com.imagehosting.dao.ImageDao;
import com.imagehosting.model.dto.ImageQueryDTO;
import com.imagehosting.model.entity.Image;
import com.imagehosting.model.vo.ImageUploadVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import com.imagehosting.service.ImageService;
import com.imagehosting.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;
    private final StorageService storageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImageUploadVO uploadImage(MultipartFile file) {
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "只能上传图片文件");
            }

            // 验证文件大小
            long size = file.getSize();
            if (size > 10 * 1024 * 1024) { // 10MB
                throw new BusinessException(ResultCode.PARAM_ERROR, "图片大小不能超过10MB");
            }

            // 获取当前用户ID
            Long userId;
            try {
                userId = SecurityUtil.getCurrentUserId();
                log.debug("成功获取上传用户ID: {}", userId);
            } catch (Exception e) {
                log.warn("获取当前用户ID失败，使用默认用户ID: {}", e.getMessage());
                // 使用默认用户ID (1)
                userId = 1L;
            }

            // 计算文件MD5
            String md5 = DigestUtils.md5DigestAsHex(file.getBytes());

            // 检查是否已存在相同MD5的图片
            Image existingImage = imageDao.findByUserIdAndMd5(userId, md5);
            if (existingImage != null && existingImage.getDeleted() == 0) {
                // 直接返回已存在的图片信息
                ImageUploadVO result = new ImageUploadVO();
                result.setId(existingImage.getId());
                result.setName(existingImage.getName());
                result.setUrl(storageService.getFileUrl(existingImage.getPath()));
                result.setSize(existingImage.getSize());
                result.setWidth(existingImage.getWidth());
                result.setHeight(existingImage.getHeight());
                log.info("上传图片已存在，直接返回: {}", result);
                return result;
            }

            // 获取图片尺寸
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;

            // 上传文件到存储
            String filePath = storageService.uploadFile(file, "images");

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            
            // 生成图片记录
            Image image = Image.builder()
                    .userId(userId)
                    .name(originalFilename)
                    .originalName(originalFilename)
                    .path(filePath)
                    .url(storageService.getFileUrl(filePath))
                    .md5(md5)
                    .size(size)
                    .width(width)
                    .height(height)
                    .mimeType(contentType)
                    .accessCount(0L)
                    .deleted(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            // 保存图片记录
            imageDao.insert(image);

            // 返回结果
            return ImageUploadVO.builder()
                    .id(image.getId())
                    .name(image.getName())
                    .url(image.getUrl())
                    .size(image.getSize())
                    .width(image.getWidth())
                    .height(image.getHeight())
                    .build();
        } catch (IOException e) {
            log.error("上传图片失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "上传图片失败: " + e.getMessage());
        }
    }

    @Override
    public PageVO<ImageVO> getImageList(ImageQueryDTO queryDTO) {
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();

        // 查询总数
        Long total = imageDao.countByUserId(userId, queryDTO.getKeyword());

        // 查询列表
        List<Image> imageList = imageDao.findByUserId(
                userId,
                queryDTO.getKeyword(),
                (queryDTO.getPage() - 1) * queryDTO.getSize(),
                queryDTO.getSize(),
                queryDTO.getSortField(),
                queryDTO.getSortOrder()
        );

        // 转换为VO
        List<ImageVO> voList = imageList.stream().map(this::convertToVO).collect(Collectors.toList());

        // 返回分页结果
        return PageVO.of(voList, total, queryDTO.getPage(), queryDTO.getSize());
    }

    @Override
    public ImageVO getImageById(Long id) {
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();

        // 查询图片
        Image image = imageDao.findById(id);
        if (image == null || image.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "图片不存在");
        }

        // 验证图片所有权
        if (!image.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该图片");
        }

        // 转换为VO并返回
        return convertToVO(image);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long id) {
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();

        // 查询图片
        Image image = imageDao.findById(id);
        if (image == null || image.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "图片不存在");
        }

        // 验证图片所有权
        if (!image.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该图片");
        }

        // 逻辑删除图片记录
        imageDao.delete(id);

        // 尝试从存储中删除文件（根据业务需求决定是否物理删除）
        storageService.deleteFile(image.getPath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String accessImage(Long id) {
        // 查询图片
        Image image = imageDao.findById(id);
        if (image == null || image.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "图片不存在");
        }

        // 增加访问次数
        imageDao.incrementAccessCount(id);

        // 返回图片URL
        return image.getUrl();
    }

    /**
     * 转换为VO
     *
     * @param image 图片实体
     * @return 图片VO
     */
    private ImageVO convertToVO(Image image) {
        ImageVO vo = new ImageVO();
        BeanUtils.copyProperties(image, vo);
        return vo;
    }
} 