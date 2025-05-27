package com.imagehosting.service.impl;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import com.imagehosting.common.util.SecurityUtil;
import com.imagehosting.dao.ImageDao;
import com.imagehosting.dao.UserDao;
import com.imagehosting.model.dto.AdminUserUpdateDTO;
import com.imagehosting.model.dto.ImageStatDTO;
import com.imagehosting.model.dto.UserQueryDTO;
import com.imagehosting.model.entity.Image;
import com.imagehosting.model.entity.User;
import com.imagehosting.model.vo.AdminStatsVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import com.imagehosting.model.vo.UserVO;
import com.imagehosting.service.AdminService;
import com.imagehosting.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserDao userDao;
    private final ImageDao imageDao;
    private final StorageService storageService;

    @Override
    public AdminStatsVO getAdminStats() {
        // 验证管理员权限
        validateAdminRole();

        // 查询统计数据
        Long totalUsers = userDao.countTotal();
        Long newUsersToday = userDao.countTodayNew();
        Long totalImages = imageDao.countAllImages(null);
        Long uploadedToday = imageDao.countTodayUploaded();
        Long totalStorage = imageDao.sumStorageSize();
        List<ImageStatDTO> weekStats = imageDao.countWeekStats();
        List<ImageStatDTO> monthStats = imageDao.countMonthStats();

        // 构建并返回结果
        return AdminStatsVO.builder()
                .totalUsers(totalUsers)
                .newUsersToday(newUsersToday)
                .totalImages(totalImages)
                .uploadedToday(uploadedToday)
                .totalStorage(totalStorage)
                .weekStats(weekStats)
                .monthStats(monthStats)
                .build();
    }

    @Override
    public PageVO<UserVO> getUserList(UserQueryDTO queryDTO) {
        // 验证管理员权限
        validateAdminRole();

        try {
            log.debug("开始查询用户列表，参数: {}", queryDTO);
            
            // 查询总数
            Long total = userDao.count(
                    queryDTO.getKeyword(),
                    queryDTO.getRoleType(),
                    queryDTO.getStatus()
            );
            log.debug("查询到用户总数: {}", total);

            // 查询列表
            List<User> userList = userDao.findList(
                    queryDTO.getKeyword(),
                    queryDTO.getRoleType(),
                    queryDTO.getStatus(),
                    (queryDTO.getPage() - 1) * queryDTO.getSize(),
                    queryDTO.getSize(),
                    queryDTO.getSortField(),
                    queryDTO.getSortOrder()
            );
            log.debug("查询到用户列表，大小: {}", userList.size());

            // 转换为VO
            List<UserVO> voList = userList.stream().map(this::convertToUserVO).collect(Collectors.toList());

            // 返回分页结果
            return PageVO.of(voList, total, queryDTO.getPage(), queryDTO.getSize());
        } catch (Exception e) {
            log.error("查询用户列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FAILED, "查询用户列表失败: " + e.getMessage());
        }
    }

    @Override
    public UserVO getUserById(Long id) {
        // 验证管理员权限
        validateAdminRole();

        // 查询用户
        User user = userDao.findById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 转换为VO并返回
        return convertToUserVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AdminUserUpdateDTO updateDTO) {
        // 验证管理员权限
        validateAdminRole();

        // 查询用户
        User user = userDao.findById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 更新用户信息
        user.setNickname(updateDTO.getNickname());
        user.setEmail(updateDTO.getEmail());
        user.setPhone(updateDTO.getPhone());
        user.setRoleType(updateDTO.getRoleType());
        user.setStatus(updateDTO.getStatus());

        // 保存更新
        userDao.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 验证管理员权限
        validateAdminRole();

        // 查询用户
        User user = userDao.findById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }

        // 禁止删除自己
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (id.equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "不能删除自己的账户");
        }

        // 删除用户
        userDao.delete(id);
    }

    @Override
    public PageVO<ImageVO> getAllImageList(Integer page, Integer size, Long userId) {
        // 验证管理员权限
        validateAdminRole();

        try {
            log.debug("开始查询所有图片列表，参数: page={}, size={}, userId={}", page, size, userId);
            
            // 查询总数
            Long total = imageDao.countAllImages(userId);
            log.debug("查询到图片总数: {}", total);

            // 查询列表
            List<Image> imageList = imageDao.findAllImages(
                    userId,
                    (page - 1) * size,
                    size
            );
            log.debug("查询到图片列表，大小: {}", imageList.size());

            // 转换为VO
            List<ImageVO> voList = imageList.stream().map(this::convertToImageVO).collect(Collectors.toList());

            // 返回分页结果
            return PageVO.of(voList, total, page, size);
        } catch (Exception e) {
            log.error("查询所有图片列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FAILED, "查询所有图片列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long id) {
        // 验证管理员权限
        validateAdminRole();

        // 查询图片
        Image image = imageDao.findById(id);
        if (image == null || image.getDeleted() == 1) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "图片不存在");
        }

        // 逻辑删除图片记录
        imageDao.delete(id);

        // 尝试从存储中删除文件
        storageService.deleteFile(image.getPath());
    }

    /**
     * 验证管理员权限
     */
    private void validateAdminRole() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            log.debug("验证管理员权限，当前用户ID: {}", userId);
            
            User user = userDao.findById(userId);
            if (user == null) {
                log.warn("用户不存在，ID: {}", userId);
                throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
            }
            
            log.debug("用户信息: id={}, username={}, roleType={}", user.getId(), user.getUsername(), user.getRoleType());
            
            if (user.getRoleType() != 1) {
                log.warn("用户不是管理员，ID: {}, roleType: {}", userId, user.getRoleType());
                throw new BusinessException(ResultCode.FORBIDDEN, "无管理员权限");
            }
            
            log.debug("管理员权限验证通过，用户ID: {}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("验证管理员权限时发生错误: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.UNAUTHORIZED, "验证管理员权限失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 转换为图片VO
     *
     * @param image 图片实体
     * @return 图片VO
     */
    private ImageVO convertToImageVO(Image image) {
        ImageVO vo = new ImageVO();
        BeanUtils.copyProperties(image, vo);
        return vo;
    }
} 