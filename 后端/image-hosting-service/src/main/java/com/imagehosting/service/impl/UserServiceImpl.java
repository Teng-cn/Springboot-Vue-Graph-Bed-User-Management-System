package com.imagehosting.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.imagehosting.dao.ImageDao;
import com.imagehosting.dao.UserDao;
import com.imagehosting.model.dto.LoginDTO;
import com.imagehosting.model.dto.RegisterDTO;
import com.imagehosting.model.dto.UpdatePasswordDTO;
import com.imagehosting.model.dto.UpdateUserDTO;
import com.imagehosting.model.entity.User;
import com.imagehosting.model.vo.LoginVO;
import com.imagehosting.model.vo.UserStatsVO;
import com.imagehosting.model.vo.UserVO;
import com.imagehosting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ImageDao imageDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.domain}")
    private String domain;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userDao.findByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .nickname(registerDTO.getUsername())
                .roleType(0) // 普通用户
                .status(0)   // 正常状态
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 保存用户
        userDao.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 根据用户名查找用户
        User user = userDao.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 返回登录信息
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roleType(user.getRoleType())
                .build();

        return LoginVO.builder()
                .user(userVO)
                .build();
    }

    @Override
    public UserVO getCurrentUserInfo() {
        // 获取当前登录用户ID
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 查询用户信息
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 转换为VO对象
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roleType(user.getRoleType())
                .createTime(user.getCreateTime())
                .build();
    }

    @Override
    public UserStatsVO getUserStats() {
        // 获取当前登录用户ID
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 查询用户统计信息
        return UserStatsVO.builder()
                .totalImages(imageDao.countByUserId(userId, null))
                .totalStorage(imageDao.sumStorageSize())
                .totalAccess(imageDao.countAllImages(userId))
                .todayUploaded(imageDao.countTodayUploaded())
                .build();
    }

    @Override
    @Transactional
    public void updateUserInfo(UpdateUserDTO updateUserDTO) {
        // 获取当前登录用户ID
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 查询用户信息
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        user.setNickname(updateUserDTO.getNickname());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());
        user.setUpdateTime(LocalDateTime.now());

        // 保存更新
        userDao.update(user);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        // 获取当前登录用户ID
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 查询用户信息
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());

        // 保存更新
        userDao.update(user);
    }

    @Override
    @Transactional
    public String uploadAvatar(MultipartFile file) {
        // 获取当前登录用户ID
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // 查询用户信息
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片文件");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String fileName = "avatars/" + userId + "/" + UUID.randomUUID().toString() + extension;

        // 上传到阿里云OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());
            ossClient.putObject(putObjectRequest);
            
            // 生成访问URL
            String avatarUrl = domain + "/" + fileName;

            // 更新用户头像
            user.setAvatar(avatarUrl);
            user.setUpdateTime(LocalDateTime.now());
            userDao.update(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("上传头像失败", e);
        } finally {
            ossClient.shutdown();
        }
    }
} 