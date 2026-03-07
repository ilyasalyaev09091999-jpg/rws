package com.route.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Redis-кэширования для маршрутов в {@code route-api}.
 * <p>
 * Сервис разделяет кэши на два типа:
 * </p>
 * <ul>
 *   <li>{@code route-port-port} — стабильные маршруты между портами
 *   с более длинным TTL;</li>
 *   <li>{@code route-ad-hoc} — произвольные маршруты с коротким TTL.</li>
 * </ul>
 */
@Configuration
public class RedisConfig {

    /**
     * Базовая Redis-конфигурация кэша по умолчанию.
     *
     * @return конфигурация с TTL 1 день и отключенным кэшированием null.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues();
    }

    /**
     * Конфигурация кэша маршрутов между портами.
     *
     * @return конфигурация с TTL 14 дней.
     */
    @Bean
    public RedisCacheConfiguration portToPortCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(14))
                .disableCachingNullValues();
    }

    /**
     * Конфигурация кэша произвольных маршрутов.
     *
     * @return конфигурация с TTL 3 дня.
     */
    @Bean
    public RedisCacheConfiguration adHocCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(3))
                .disableCachingNullValues();
    }

    /**
     * Создаёт {@link RedisCacheManager} с именованными конфигурациями кэша.
     *
     * @param redisConnectionFactory фабрика Redis-соединений.
     * @return менеджер кэшей route-api.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("route-port-port", portToPortCacheConfig());
        cacheConfigs.put("route-ad-hoc", adHocCacheConfig());

        return RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
