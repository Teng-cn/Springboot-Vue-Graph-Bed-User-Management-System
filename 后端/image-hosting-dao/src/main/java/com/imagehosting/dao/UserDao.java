package com.imagehosting.dao;

import com.imagehosting.model.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 */
public interface UserDao {

    /**
     * 插入用户
     *
     * @param user 用户
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户
     *
     * @param user 用户
     * @return 影响行数
     */
    int update(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户
     */
    User findById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    User findByEmail(String email);

    /**
     * 分页查询用户列表
     *
     * @param keyword  关键字
     * @param roleType 角色类型
     * @param status   状态
     * @param offset   偏移量
     * @param limit    限制
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 用户列表
     */
    List<User> findList(
            @Param("keyword") String keyword,
            @Param("roleType") Integer roleType,
            @Param("status") Integer status,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    /**
     * 查询用户总数
     *
     * @param keyword  关键字
     * @param roleType 角色类型
     * @param status   状态
     * @return 用户总数
     */
    Long count(
            @Param("keyword") String keyword,
            @Param("roleType") Integer roleType,
            @Param("status") Integer status
    );

    /**
     * 查询总用户数
     *
     * @return 总用户数
     */
    Long countTotal();

    /**
     * 查询今天新增用户数
     *
     * @return 今天新增用户数
     */
    Long countTodayNew();
} 