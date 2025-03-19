package org.leverx.ratingapp.repositories.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


/**
 * Base abstract repository for Redis operations with time-to-live (TTL) functionality.
 * Provides common CRUD operations for Redis key-value pairs with configurable prefixes and expiration times.
 * Each extending repository must specify its own key prefix and TTL settings.
 */
@RequiredArgsConstructor
public abstract class BaseRedisRepository {
    protected final RedisTemplate<String, String> redisTemplate;

    /**
     * Gets the prefix used for Redis keys in the implementing repository.
     * @return the prefix string that will be prepended to all keys
     */
    protected abstract String getPrefix();

    /**
     * Gets the time-to-live duration for stored values.
     * @return the TTL value in units specified by {@link #getTTLUnit()}
     */
    protected abstract long getTTL();

    /**
     * Gets the time unit for the TTL duration.
     * @return the {@link TimeUnit} for the TTL value
     */
    protected abstract TimeUnit getTTLUnit();

    /**
     * Builds a complete Redis key by combining the prefix with the identifier.
     * @param identifier the unique identifier for the key
     * @return the complete Redis key
     */
    private String buildKey(String identifier) {
        return getPrefix() + identifier;
    }

    /**
     * Saves a value in Redis with the specified identifier and configured TTL.
     * @param identifier the unique identifier for the key
     * @param value the value to store
     */
    public void save(String identifier, String value) {
        String key = buildKey(identifier);
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, getTTL(), getTTLUnit());
    }

    /**
     * Retrieves a value from Redis by its identifier.
     * @param identifier the unique identifier for the key
     * @return the stored value, or null if not found
     */
    public String get(String identifier) {
        return redisTemplate.opsForValue().get(buildKey(identifier));
    }

    /**
     * Removes a value from Redis by its identifier.
     * @param identifier the unique identifier for the key to remove
     */
    public void remove(String identifier) {
        redisTemplate.delete(buildKey(identifier));
    }
}