package org.jiaqi.photoserv.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.jiaqi.photoserv.entity.CommonResponse;
import org.jiaqi.photoserv.entity.PhotoListResponse;
import org.jiaqi.photoserv.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 照片服务控制器
 * 提供照片上传和获取的REST接口
 *
 * @author jiaqi.zhang
 * @version 1.0
 * @date 2024-12-09
 */
@Slf4j
@RestController()
@RequestMapping("/photos")
public class photoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private RateLimiter getPhotosRateLimiter;

    @GetMapping("/getPhotos")
    public ResponseEntity<CommonResponse<PhotoListResponse>> getPhotos() {
        String requestId = UUID.randomUUID().toString();
        log.info("开始获取照片列表, requestId: {}", requestId);
        
        try {
            return RateLimiter.decorateSupplier(getPhotosRateLimiter, () -> {
                try {
                    PhotoListResponse photoListResponse = photoService.getPhotos();

                    CommonResponse<PhotoListResponse> response = new CommonResponse<>();
                    response.setSuccess(true);
                    response.setTimestamp(LocalDateTime.now());
                    response.setRequestId(requestId);
                    response.setResponse(photoListResponse);

                    log.info("照片列表获取成功, requestId: {}", requestId);
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    log.error("获取照片列表失败, requestId: {}, error: {}", requestId, e.getMessage(), e);
                    
                    CommonResponse<PhotoListResponse> response = new CommonResponse<>();
                    response.setSuccess(false);
                    response.setTimestamp(LocalDateTime.now());
                    response.setRequestId(requestId);
                    return ResponseEntity.ok(response);
                }
            }).get();
        } catch (RequestNotPermitted e) {
            log.warn("请求被限流, requestId: {}", requestId);
            
            CommonResponse<PhotoListResponse> response = new CommonResponse<>();
            response.setSuccess(false);
            response.setTimestamp(LocalDateTime.now());
            response.setRequestId(requestId);
            response.setResponse(null);
            
            return ResponseEntity.status(429).body(response);  // 429 Too Many Requests
        }
    }

    @PostMapping("/upload")
    public CommonResponse<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        String requestId = UUID.randomUUID().toString();
        log.info("开始上传照片, fileName: {}, requestId: {}", file.getOriginalFilename(), requestId);
        
        try {
            String photoUrl = photoService.uploadPhoto(file);
            
            CommonResponse<String> response = new CommonResponse<>();
            response.setSuccess(true);
            response.setTimestamp(LocalDateTime.now());
            response.setRequestId(requestId);
            response.setResponse(photoUrl);
            
            log.info("照片上传完成, requestId: {}", requestId);
            return response;
        } catch (Exception e) {
            log.error("照片上传失败, fileName: {}, requestId: {}, error: {}", 
                    file.getOriginalFilename(), requestId, e.getMessage(), e);
            
            CommonResponse<String> response = new CommonResponse<>();
            response.setSuccess(false);
            response.setTimestamp(LocalDateTime.now());
            response.setRequestId(requestId);
            return response;
        }
    }
}
