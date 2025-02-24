package org.jiaqi.photoserv.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {
    
    @Value("${resilience4j.ratelimiter.instances.getPhotos.limitForPeriod}")
    private int limitForPeriod;
    
    @Value("${resilience4j.ratelimiter.instances.getPhotos.limitRefreshPeriod}")
    private String limitRefreshPeriod;
    
    @Value("${resilience4j.ratelimiter.instances.getPhotos.timeoutDuration}")
    private String timeoutDuration;
    
    @Bean
    public RateLimiter getPhotosRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.parse(limitRefreshPeriod))
                .limitForPeriod(limitForPeriod)
                .timeoutDuration(Duration.parse(timeoutDuration))
                .build();
        
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("getPhotos");
    }
} 