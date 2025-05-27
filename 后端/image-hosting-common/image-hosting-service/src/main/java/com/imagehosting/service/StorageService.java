package com.imagehosting.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     *
     * @param file      文件
     * @param directory 目录
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 文件访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 生成文件名
     *
     * @param originalFilename 原始文件名
     * @return 新文件名
     */
    String generateFileName(String originalFilename);
} 