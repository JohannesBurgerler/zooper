package com.trade4life.zooper.scheduler;

import com.trade4life.zooper.service.ActuatorMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActuatorMetricScheduler {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ActuatorMetricsService actuatorMetricsService;

    private final String REDIS_KEY = "actuator:metrics";
}
