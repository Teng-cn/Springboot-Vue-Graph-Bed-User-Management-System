package com.imagehosting.service.impl;

import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import com.imagehosting.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地存储服务实现
 */
@Slf4j
@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${storage.local.path}")
    private String storagePath;

    @Value("${storage.local.url-prefix}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }

        // 生成日期目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 完整目录路径
        String fullDirectory = storagePath + File.separator + directory + File.separator + datePath;
        
        // 确保目录存在
        Path directoryPath = Paths.get(fullDirectory);
        Files.createDirectories(directoryPath);
        
        // 生成新文件名
        String newFileName = generateFileName(file.getOriginalFilename());
        
        // 完整文件路径
        String filePath = fullDirectory + File.separator + newFileName;
        Path fileFullPath = Paths.get(filePath);
        
        // 写入文件
        file.transferTo(fileFullPath.toFile());
        
        // 返回文件路径（相对于存储根目录）
        return directory + "/" + datePath + "/" + newFileName;
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(storagePath, filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return urlPrefix + "/" + filePath;
    }

    @Override
    public String generateFileName(String originalFilename) {
        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 使用UUID生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 生成时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // 生成MD5
        String md5 = DigestUtils.md5DigestAsHex((uuid + timestamp).getBytes()).substring(0, 16);
        
        // 返回新文件名
        return md5 + extension;
    }
} 