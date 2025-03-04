package org.leverx.ratingapp.repositories.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ConfirmationTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "user_token:";
    private static final long TOKEN_TTL_HOURS = 24;

    public ConfirmationTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String email, String token) {
        String key = TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, token);
        redisTemplate.expire(key, TOKEN_TTL_HOURS, TimeUnit.HOURS);
    }

    public String getToken(String email) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + email);
    }

    public void removeToken(String email) {
        redisTemplate.delete(TOKEN_PREFIX + email);
    }
}
