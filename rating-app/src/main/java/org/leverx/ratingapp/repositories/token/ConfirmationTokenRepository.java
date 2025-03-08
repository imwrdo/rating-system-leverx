package org.leverx.ratingapp.repositories.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ConfirmationTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "user_token:";
    private static final long TOKEN_TTL_HOURS = 24;
    private static final String RESET_PREFIX = "reset_code:";
    private static final long RESET_CODE_TTL_MINUTES = 15;

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

    public void saveResetCode(String email, String code) {
        String key = RESET_PREFIX + email;
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, RESET_CODE_TTL_MINUTES, TimeUnit.MINUTES);
    }

    public String getResetCode(String email) {
        return redisTemplate.opsForValue().get(RESET_PREFIX + email);
    }

    public void removeResetCode(String email) {
        redisTemplate.delete(RESET_PREFIX + email);
    }
}
