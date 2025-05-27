package com.imagehosting.service;

import com.imagehosting.model.dto.ImageQueryDTO;
import com.imagehosting.model.vo.ImageUploadVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片服务接口
 */
public interface ImageService {

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 上传结果
     */
    ImageUploadVO uploadImage(MultipartFile file);

    /**
     * 分页查询图片列表
     *
     * @param queryDTO 查询条件
     * @return 图片列表
     */
    PageVO<ImageVO> getImageList(ImageQueryDTO queryDTO);

    /**
     * 获取图片详情
     *
     * @param id 图片ID
     * @return 图片详情
     */
    ImageVO getImageById(Long id);

    /**
     * 删除图片
     *
     * @param id 图片ID
     */
    void deleteImage(Long id);

    /**
     * 访问图片
     *
     * @param id 图片ID
     * @return 图片URL
     */
    String accessImage(Long id);
} 