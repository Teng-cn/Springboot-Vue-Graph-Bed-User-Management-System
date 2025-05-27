package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import com.imagehosting.model.dto.LoginDTO;
import com.imagehosting.model.dto.RegisterDTO;
import com.imagehosting.model.dto.UpdatePasswordDTO;
import com.imagehosting.model.dto.UpdateUserDTO;
import com.imagehosting.model.entity.User;
import com.imagehosting.model.vo.LoginVO;
import com.imagehosting.model.vo.UserStatsVO;
import com.imagehosting.model.vo.UserVO;
import com.imagehosting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        return Result.success(userService.getCurrentUserInfo());
    }

    /**
     * 获取用户统计信息
     *
     * @return 用户统计信息
     */
    @Operation(summary = "获取用户统计信息")
    @GetMapping("/stats")
    public Result<UserStatsVO> getUserStats() {
        return Result.success(userService.getUserStats());
    }

    /**
     * 更新用户信息
     *
     * @param updateUserDTO 用户信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody UpdateUserDTO updateUserDTO) {
        userService.updateUserInfo(updateUserDTO);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param updatePasswordDTO 密码信息
     * @return 修改结果
     */
    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(updatePasswordDTO);
        return Result.success();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success(Map.of("url", avatarUrl));
    }
} 