package com.imagehosting.dao;

import com.imagehosting.model.entity.Image;
import com.imagehosting.model.dto.ImageStatDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图片数据访问接口
 */
public interface ImageDao {

    /**
     * 插入图片
     *
     * @param image 图片
     * @return 影响行数
     */
    int insert(Image image);

    /**
     * 更新图片
     *
     * @param image 图片
     * @return 影响行数
     */
    int update(Image image);

    /**
     * 删除图片（逻辑删除）
     *
     * @param id 图片ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID查询图片
     *
     * @param id 图片ID
     * @return 图片
     */
    Image findById(Long id);

    /**
     * 根据用户ID和MD5查询图片
     *
     * @param userId 用户ID
     * @param md5    MD5值
     * @return 图片
     */
    Image findByUserIdAndMd5(@Param("userId") Long userId, @Param("md5") String md5);

    /**
     * 根据用户ID查询图片列表
     *
     * @param userId    用户ID
     * @param keyword   关键字
     * @param offset    偏移量
     * @param limit     限制
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 图片列表
     */
    List<Image> findByUserId(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    /**
     * 根据用户ID统计图片数量
     *
     * @param userId  用户ID
     * @param keyword 关键字
     * @return 图片数量
     */
    Long countByUserId(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 增加访问次数
     *
     * @param id 图片ID
     * @return 影响行数
     */
    int incrementAccessCount(Long id);
    
    /**
     * 查询所有图片列表（管理员用）
     *
     * @param userId 用户ID（可选）
     * @param offset 偏移量
     * @param limit  限制
     * @return 图片列表
     */
    List<Image> findAllImages(
            @Param("userId") Long userId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    
    /**
     * 统计所有图片数量（管理员用）
     *
     * @param userId 用户ID（可选）
     * @return 图片数量
     */
    Long countAllImages(@Param("userId") Long userId);
    
    /**
     * 统计总存储空间大小
     *
     * @return 总存储空间大小（字节）
     */
    Long sumStorageSize();
    
    /**
     * 统计今日上传图片数量
     *
     * @return 今日上传图片数量
     */
    Long countTodayUploaded();
    
    /**
     * 统计过去7天的每日上传数量
     *
     * @return 每日统计数据
     */
    List<ImageStatDTO> countWeekStats();
    
    /**
     * 统计过去30天的每日上传数量
     *
     * @return 每日统计数据
     */
    List<ImageStatDTO> countMonthStats();
} 