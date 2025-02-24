package org.jiaqi.photoserv.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jiaqi.photoserv.entity.PhotoListResponse;
import org.jiaqi.photoserv.service.MinioService;
import org.jiaqi.photoserv.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 照片服务实现类
 * 实现照片的上传和获取等业务逻辑
 *
 * @author jiaqi.zhang
 * @version 1.0
 * @date 2024-12-09
 */
@Slf4j
@Service
public class PhotoServiceImpl implements PhotoService {

    @Value("${photo.upload.max-size:10485760}") // 默认10MB
    private long maxFileSize;

    @Value("${photo.upload.allowed-types:image/jpeg,image/png,image/gif}")
    private String allowedTypes;

    @Autowired
    private MinioService minioService;

    @Override
    public PhotoListResponse getPhotos() throws Exception {
        log.debug("开始获取照片列表");
        List<String> photoUrls = minioService.getAllPhotos();
        
        PhotoListResponse response = new PhotoListResponse();
        response.setSize(photoUrls.size());
        response.setData(photoUrls);
        
        log.debug("获取到 {} 张照片", photoUrls.size());
        return response;
    }

    @Override
    public String uploadPhoto(MultipartFile file) throws Exception {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 验证文件名
        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                String.format("文件大小不能超过%dMB", maxFileSize / 1024 / 1024));
        }

        // 验证文件类型
        String contentType = file.getContentType();
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypeList.contains(Objects.requireNonNull(contentType))) {
            throw new IllegalArgumentException(
                String.format("不支持的文件类型，仅支持: %s", allowedTypes));
        }

        log.debug("开始上传照片: {}, 大小: {}KB, 类型: {}", 
            fileName, file.getSize()/1024, contentType);

        try {
            String photoUrl = minioService.uploadFile(file);
            log.debug("照片上传成功: {}", photoUrl);
            return photoUrl;
        } catch (Exception e) {
            log.error("照片上传失败: {}", fileName, e);
            throw new RuntimeException("照片上传失败: " + e.getMessage());
        }
    }
} 