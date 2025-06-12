package com.trade4life.zooper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public void blacklistToken(String token){
        try{
            String key = BLACKLIST_PREFIX + token;

            long ttlSeconds = jwtExpirationMs / 1000;

            redisTemplate.opsForValue().set(key, "true", ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e){
            logger.debug("There has been an error while blacklisting the token: {} error: {}", token, e.getMessage());
           e.printStackTrace();
        }
    }

    public boolean isTokenBlacklisted(String token){
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(key));
        } catch(Exception e){
            logger.debug("There has been an error whilechecking the  blacklisted token: {} error: {}", token, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void removeToken(String token){
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.delete(key);
        } catch(Exception e){
            logger.debug("There has been an error while removing the  blacklisted token: {} error: {}", token, e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isRedisConnected() {
        try{
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch(Exception e){
            logger.error("Redis is not connected error: {}",  e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
