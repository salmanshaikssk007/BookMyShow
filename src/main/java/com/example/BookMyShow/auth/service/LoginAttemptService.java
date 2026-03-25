package com.example.BookMyShow.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final String ATTEMPTS_PREFIX = "login:attempts:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final StringRedisTemplate redisTemplate;

    public void checkBlocked(String username) {
        String key = ATTEMPTS_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && Integer.parseInt(value) >= MAX_ATTEMPTS) {
            Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            throw new RuntimeException(
                    "Account temporarily locked due to too many failed attempts. Try again in " + ttlSeconds + " seconds."
            );
        }
    }

    public void recordFailure(String username) {
        String key = ATTEMPTS_PREFIX + username;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1) {
            // Set TTL only on the first failure to start the window
            redisTemplate.expire(key, LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }
    }

    public void resetAttempts(String username) {
        redisTemplate.delete(ATTEMPTS_PREFIX + username);
    }
}
