package org.leverx.ratingapp.repositories.redis;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ConfirmationTokenRedisRepository extends BaseRedisRepository{
    private static final String TOKEN_PREFIX = "user_token:";  // Prefix for confirmation token keys
    private static final long TOKEN_TTL_HOURS = 24;  // Token TTL in hours

    public ConfirmationTokenRedisRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getPrefix() {
        return TOKEN_PREFIX;
    }
    @Override
    protected long getTTL() {
        return TOKEN_TTL_HOURS;
    }
    @Override
    protected TimeUnit getTTLUnit() {
        return TimeUnit.HOURS;
    }

}

