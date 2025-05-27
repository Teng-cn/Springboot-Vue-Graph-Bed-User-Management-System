package com.imagehosting.service;

import com.imagehosting.model.dto.LoginDTO;
import com.imagehosting.model.dto.RegisterDTO;
import com.imagehosting.model.dto.UpdatePasswordDTO;
import com.imagehosting.model.dto.UpdateUserDTO;
import com.imagehosting.model.entity.User;
import com.imagehosting.model.vo.LoginVO;
import com.imagehosting.model.vo.UserStatsVO;
import com.imagehosting.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 获取用户统计信息
     *
     * @return 用户统计信息
     */
    UserStatsVO getUserStats();

    /**
     * 更新用户信息
     *
     * @param updateUserDTO 用户信息
     */
    void updateUserInfo(UpdateUserDTO updateUserDTO);

    /**
     * 修改密码
     *
     * @param updatePasswordDTO 密码信息
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(MultipartFile file);
} 