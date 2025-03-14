package org.leverx.ratingapp.repositories.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Repository
public class PendingCommentRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PENDING_COMMENT_PREFIX = "pending_comment:";
    private static final long COMMENT_TTL_MINUTES = 30;

    public PendingCommentRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void savePendingComment(String email, String commentJson) {
        String key = PENDING_COMMENT_PREFIX + email;
        redisTemplate.opsForValue().set(key, commentJson);
        redisTemplate.expire(key, COMMENT_TTL_MINUTES, TimeUnit.MINUTES);
    }

    public String getPendingComment(String email) {
        return redisTemplate.opsForValue().get(PENDING_COMMENT_PREFIX + email);
    }

    public void removePendingComment(String email) {
        redisTemplate.delete(PENDING_COMMENT_PREFIX + email);
    }
}
