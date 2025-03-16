package org.leverx.ratingapp.repositories.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

@Repository
public class PendingCommentRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PENDING_COMMENT_PREFIX = "pending_comment:";  // Prefix for pending comments
    private static final long COMMENT_TTL_MINUTES = 30;  // TTL in minutes for pending comments

    // Constructor to inject RedisTemplate
    public PendingCommentRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Save the pending comment in Redis and set expiration time
    public void savePendingComment(String email, String commentJson) {
        String key = PENDING_COMMENT_PREFIX + email;  // Construct key using email
        redisTemplate.opsForValue().set(key, commentJson);  // Store comment (as JSON) in Redis
        redisTemplate.expire(key, COMMENT_TTL_MINUTES, TimeUnit.MINUTES);  // Set TTL (30 minutes)
    }

    // Retrieve the pending comment for the given email
    public String getPendingComment(String email) {
        return redisTemplate.opsForValue().get(PENDING_COMMENT_PREFIX + email);  // Get comment by key
    }

    // Remove the pending comment from Redis for the given email
    public void removePendingComment(String email) {
        redisTemplate.delete(PENDING_COMMENT_PREFIX + email);  // Delete key from Redis
    }
}
