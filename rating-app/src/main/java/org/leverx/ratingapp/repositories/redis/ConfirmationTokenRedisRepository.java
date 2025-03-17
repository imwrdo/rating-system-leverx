package org.leverx.ratingapp.repositories.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class ConfirmationTokenRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "user_token:";  // Prefix for confirmation token keys
    private static final long TOKEN_TTL_HOURS = 24;  // Token TTL in hours

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

}

