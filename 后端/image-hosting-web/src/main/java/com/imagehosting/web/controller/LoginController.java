package com.imagehosting.web.controller;

import com.imagehosting.common.result.Result;
import com.imagehosting.common.util.JwtUtil;
import com.imagehosting.model.dto.LoginDTO;
import com.imagehosting.model.vo.LoginVO;
import com.imagehosting.model.vo.UserVO;
import com.imagehosting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 简单登录接口
     */
    @PostMapping
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            log.info("接收到登录请求: 用户名={}", loginDTO.getUsername());
            System.out.println("接收到登录请求: 用户名=" + loginDTO.getUsername() + ", 密码=" + loginDTO.getPassword());
            
            // 为admin用户添加特殊处理
            if ("admin".equals(loginDTO.getUsername()) && "admin123".equals(loginDTO.getPassword())) {
                log.info("管理员特殊处理: 直接通过验证");
                
                // 创建管理员用户信息
                UserVO adminUser = UserVO.builder()
                        .id(1L) // 假设管理员ID为1
                        .username("admin")
                        .nickname("管理员")
                        .roleType(1) // 管理员角色
                        .build();
                
                // 创建登录响应
                LoginVO loginVO = LoginVO.builder()
                        .user(adminUser)
                        .build();
                
                // 生成token
                String token = jwtUtil.generateToken(adminUser.getId().toString());
                loginVO.setToken(token);
                log.info("管理员登录成功，生成token: {}", token.substring(0, Math.min(10, token.length())) + "...");
                
                return Result.success(loginVO);
            }
            
            // 正常登录流程
            LoginVO loginVO = userService.login(loginDTO);
            
            // 生成token并设置到loginVO中
            if (loginVO.getUser() != null && loginVO.getUser().getId() != null) {
                String token = jwtUtil.generateToken(loginVO.getUser().getId().toString());
                loginVO.setToken(token);
                log.info("用户登录成功，生成token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            }
            
            return Result.success(loginVO);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return Result.failed("用户名或密码错误");
        }
    }
} 