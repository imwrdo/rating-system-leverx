package org.leverx.ratingapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for setting up Redis as a caching and data storage solution.
 * Uses Jedis as the Redis client.
 */
@Configuration
public class RedisConfig {
    /**
     * Configures a {@link JedisConnectionFactory} for connecting to a standalone Redis server.
     * The Redis host and port are retrieved from environment variables.
     *
     * @return A configured {@link JedisConnectionFactory} instance.
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(System.getenv("REDIS_HOST"));
        config.setPort(Integer.parseInt(System.getenv("REDIS_PORT")));
        return new JedisConnectionFactory(config);
    }

    /**
     * Configures a {@link RedisTemplate} for interacting with Redis.
     * This template is set up to handle string keys and values using {@link StringRedisSerializer}.
     *
     * @param connectionFactory The Redis connection factory.
     * @return A configured {@link RedisTemplate} instance.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
