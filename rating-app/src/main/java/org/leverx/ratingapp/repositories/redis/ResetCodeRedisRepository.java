package org.leverx.ratingapp.repositories.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ResetCodeRedisRepository extends BaseRedisRepository {
    private static final String RESET_PREFIX = "reset_code:";  // Prefix for reset code keys
    private static final long RESET_CODE_TTL_MINUTES = 15;  // Reset code TTL in minutes

    public ResetCodeRedisRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getPrefix() {
        return RESET_PREFIX;
    }

    @Override
    protected long getTTL() {
        return RESET_CODE_TTL_MINUTES;
    }

    @Override
    protected TimeUnit getTTLUnit() {
        return TimeUnit.MINUTES;
    }
}
