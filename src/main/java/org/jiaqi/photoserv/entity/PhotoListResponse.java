package org.jiaqi.photoserv.entity;

import lombok.Data;
import java.util.List;

@Data
public class PhotoListResponse {
    private int size;
    private List<String> data;
} 