package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import com.imagehosting.model.dto.AdminUserUpdateDTO;
import com.imagehosting.model.dto.UserQueryDTO;
import com.imagehosting.model.vo.AdminStatsVO;
import com.imagehosting.model.vo.ImageVO;
import com.imagehosting.model.vo.PageVO;
import com.imagehosting.model.vo.UserVO;
import com.imagehosting.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@Tag(name = "管理员接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 获取管理员统计信息
     *
     * @return 统计信息
     */
    @Operation(summary = "获取管理员统计信息")
    @GetMapping("/stats")
    public Result<AdminStatsVO> getAdminStats() {
        return Result.success(adminService.getAdminStats());
    }

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/users")
    public Result<PageVO<UserVO>> getUserList(UserQueryDTO queryDTO) {
        return Result.success(adminService.getUserList(queryDTO));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/user/{id}")
    public Result<UserVO> getUserById(@PathVariable("id") Long id) {
        return Result.success(adminService.getUserById(id));
    }

    /**
     * 更新用户信息
     *
     * @param updateDTO 更新信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/user/update")
    public Result<Void> updateUser(@RequestBody AdminUserUpdateDTO updateDTO) {
        adminService.updateUser(updateDTO);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    /**
     * 查询所有图片列表
     *
     * @param page   页码
     * @param size   每页大小
     * @param userId 用户ID（可选）
     * @return 图片列表
     */
    @Operation(summary = "查询所有图片列表")
    @GetMapping("/images")
    public Result<PageVO<ImageVO>> getAllImageList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "userId", required = false) Long userId) {
        return Result.success(adminService.getAllImageList(page, size, userId));
    }

    /**
     * 删除图片
     *
     * @param id 图片ID
     * @return 删除结果
     */
    @Operation(summary = "删除图片")
    @DeleteMapping("/image/{id}")
    public Result<Void> deleteImage(@PathVariable("id") Long id) {
        adminService.deleteImage(id);
        return Result.success();
    }

    /**
     * 测试管理员权限
     *
     * @return 测试结果
     */
    @Operation(summary = "测试管理员权限")
    @GetMapping("/test-auth")
    public Result<String> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        StringBuilder sb = new StringBuilder();
        sb.append("认证信息: ").append(auth != null).append("\n");
        
        if (auth != null) {
            sb.append("主体: ").append(auth.getPrincipal()).append("\n");
            sb.append("权限: ").append(auth.getAuthorities()).append("\n");
            sb.append("已认证: ").append(auth.isAuthenticated()).append("\n");
            
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            sb.append("是管理员: ").append(isAdmin).append("\n");
        }
        
        return Result.success(sb.toString());
    }

    /**
     * 简单测试接口
     *
     * @return 测试结果
     */
    @Operation(summary = "简单测试接口")
    @GetMapping("/simple-test")
    public Result<String> simpleTest() {
        return Result.success("简单测试成功，您拥有管理员权限！");
    }
} 