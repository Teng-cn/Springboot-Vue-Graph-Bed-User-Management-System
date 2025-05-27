package com.imagehosting.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.imagehosting.common.exception.BusinessException;
import com.imagehosting.common.result.ResultCode;
import com.imagehosting.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云OSS存储服务实现类
 */
@Slf4j
@Service
@Primary
public class AliyunOssStorageServiceImpl implements StorageService {

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
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }

        // 生成日期目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 生成新文件名
        String newFileName = generateFileName(file.getOriginalFilename());
        
        // 完整OSS路径
        String ossPath = directory + "/" + datePath + "/" + newFileName;
        
        // 创建OSSClient实例
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, ossPath, file.getInputStream());
            
            // 上传文件
            ossClient.putObject(putObjectRequest);
            
            log.info("文件上传成功: {}", ossPath);
            
            // 返回文件路径
            return ossPath;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        // 创建OSSClient实例
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            // 删除文件
            ossClient.deleteObject(bucketName, filePath);
            log.info("文件删除成功: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return domain + "/" + filePath;
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