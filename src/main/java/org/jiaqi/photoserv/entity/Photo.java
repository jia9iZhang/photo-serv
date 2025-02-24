package org.jiaqi.photoserv.entity;

import lombok.Data;

@Data
public class Photo {
    private String id;          // 照片ID
    private String name;        // 照片名称
    private String url;         // 照片URL
    private String description; // 照片描述
    private Long createTime;    // 创建时间
    private String type;        // 照片类型
} 