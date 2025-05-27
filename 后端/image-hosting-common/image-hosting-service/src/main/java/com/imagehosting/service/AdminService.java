package com.imagehosting.service;

import com.imagehosting.model.dto.AdminUserUpdateDTO;
import com.imagehosting.model.dto.UserQueryDTO;
import com.imagehosting.model.vo.AdminStatsVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import com.imagehosting.model.vo.UserVO;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 获取管理员统计信息
     *
     * @return 统计信息
     */
    AdminStatsVO getAdminStats();

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    PageVO<UserVO> getUserList(UserQueryDTO queryDTO);

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long id);

    /**
     * 更新用户信息
     *
     * @param updateDTO 更新信息
     */
    void updateUser(AdminUserUpdateDTO updateDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 查询所有图片列表
     *
     * @param page  页码
     * @param size  每页大小
     * @param userId 用户ID（可选）
     * @return 图片列表
     */
    PageVO<ImageVO> getAllImageList(Integer page, Integer size, Long userId);

    /**
     * 删除图片
     *
     * @param id 图片ID
     */
    void deleteImage(Long id);
} 