package org.leverx.ratingapp.repositories.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Repository
public class PendingCommentRedisRepository extends BaseRedisRepository {
    private static final String PENDING_COMMENT_PREFIX = "pending_comment:";  // Prefix for pending comments
    private static final long COMMENT_TTL_MINUTES = 30;  // TTL in minutes for pending comments

    public PendingCommentRedisRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getPrefix() {
        return PENDING_COMMENT_PREFIX;
    }

    @Override
    protected long getTTL() {
        return COMMENT_TTL_MINUTES;
    }

    @Override
    protected TimeUnit getTTLUnit() {
        return TimeUnit.MINUTES;
    }

}
