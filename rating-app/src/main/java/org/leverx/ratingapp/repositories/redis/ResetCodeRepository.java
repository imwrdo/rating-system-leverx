package org.leverx.ratingapp.repositories.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class ResetCodeRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String RESET_PREFIX = "reset_code:";  // Prefix for reset code keys
    private static final long RESET_CODE_TTL_MINUTES = 15;  // Reset code TTL in minutes

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
