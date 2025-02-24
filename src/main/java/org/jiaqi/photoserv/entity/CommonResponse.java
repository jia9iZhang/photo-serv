package org.jiaqi.photoserv.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommonResponse<T> {
    private boolean success;
    private LocalDateTime timestamp;
    private String requestId;
    private T response;
} 