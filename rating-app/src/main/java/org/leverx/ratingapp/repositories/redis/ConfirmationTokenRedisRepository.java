package org.leverx.ratingapp.repositories.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ConfirmationTokenRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "user_token:";  // Prefix for confirmation token keys
    private static final long TOKEN_TTL_HOURS = 24;  // Token TTL in hours
    private static final String RESET_PREFIX = "reset_code:";  // Prefix for reset code keys
    private static final long RESET_CODE_TTL_MINUTES = 15;  // Reset code TTL in minutes

    // Constructor to inject the RedisTemplate dependency
    public ConfirmationTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Save the confirmation token for a given email and set expiration
    public void saveToken(String email, String token) {
        String key = TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, token);  // Store token with the key
        redisTemplate.expire(key, TOKEN_TTL_HOURS, TimeUnit.HOURS);  // Set TTL (24 hours)
    }

    // Retrieve the confirmation token for a given email
    public String getToken(String email) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + email);  // Get token by key
    }

    // Remove the confirmation token for a given email
    public void removeToken(String email) {
        redisTemplate.delete(TOKEN_PREFIX + email);  // Delete token key from Redis
    }

    // Save the password reset code for a given email and set expiration
    public void saveResetCode(String email, String code) {
        String key = RESET_PREFIX + email;
        redisTemplate.opsForValue().set(key, code);  // Store reset code with the key
        redisTemplate.expire(key, RESET_CODE_TTL_MINUTES, TimeUnit.MINUTES);  // Set TTL (15 minutes)
    }

    // Retrieve the password reset code for a given email
    public String getResetCode(String email) {
        return redisTemplate.opsForValue().get(RESET_PREFIX + email);  // Get reset code by key
    }

    // Remove the password reset code for a given email
    public void removeResetCode(String email) {
        redisTemplate.delete(RESET_PREFIX + email);  // Delete reset code key from Redis
    }
}

