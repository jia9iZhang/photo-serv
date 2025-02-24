package org.jiaqi.photoserv.service;

import org.jiaqi.photoserv.entity.PhotoListResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 照片服务接口
 * 定义照片相关的业务操作
 *
 * @author jiaqi.zhang
 * @version 1.0
 * @date 2024-12-09
 */
public interface PhotoService {
    PhotoListResponse getPhotos() throws Exception;
    String uploadPhoto(MultipartFile file) throws Exception;
} 