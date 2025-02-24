package org.jiaqi.photoserv.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;
    
    @Value("${minio.bucket}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String endpoint;

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        log.debug("开始上传文件到MinIO, fileName: {}", fileName);
        
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        
        // 生成预览URL
        String previewUrl = getPreviewUrl(fileName);
        log.debug("文件上传成功, previewUrl: {}", previewUrl);
        
        return previewUrl;
    }
    
    public List<String> getAllPhotos() throws Exception {
        log.debug("开始获取MinIO中的所有照片");
        List<String> photoUrls = new ArrayList<>();
        
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build());
        
        for (Result<Item> result : results) {
            Item item = result.get();
            // 为每个文件生成预览URL
            String previewUrl = getPreviewUrl(item.objectName());
            photoUrls.add(previewUrl);
        }
        
        log.debug("成功获取到 {} 张照片", photoUrls.size());
        return photoUrls;
    }
    
    private String getPreviewUrl(String objectName) throws Exception {
        // 生成7天有效的预览URL
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7, TimeUnit.DAYS)
                        .build());
        
        log.debug("生成预览URL: {}", url);
        return url;
    }
    
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
} 