-- 创建数据库
-- 连接信息: 用户名=root, 密码=1234
CREATE DATABASE IF NOT EXISTS image_hosting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE image_hosting;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role_type` tinyint NOT NULL DEFAULT '0' COMMENT '角色类型（0普通用户，1管理员）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '帐号状态（0正常，1禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 图片表
CREATE TABLE IF NOT EXISTS `image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '图片名称',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `path` varchar(255) NOT NULL COMMENT '存储路径',
  `url` varchar(255) NOT NULL COMMENT '访问URL',
  `md5` varchar(32) NOT NULL COMMENT 'MD5值',
  `size` bigint NOT NULL COMMENT '图片大小（字节）',
  `width` int DEFAULT NULL COMMENT '图片宽度',
  `height` int DEFAULT NULL COMMENT '图片高度',
  `mime_type` varchar(50) NOT NULL COMMENT '媒体类型',
  `access_count` bigint NOT NULL DEFAULT '0' COMMENT '访问次数',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除，1已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_md5` (`md5`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';



--管理员账号密码都是lst123
INSERT INTO `user` (`username`, `password`, `nickname`, `role_type`) 
VALUES ('lst123', '$2a$10$Q27KFPPJ0IOTHE0iyTDOBOk7WBPRorI.uh6wHhQMIcMDa.FP4kRIe', '管理员', 1);


