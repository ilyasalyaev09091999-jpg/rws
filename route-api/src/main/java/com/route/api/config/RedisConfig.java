package com.route.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    /**
     * «Кэширование маршрутов осуществляется с учётом их повторяемости и семантической значимости.
     * Наиболее устойчивыми являются маршруты между портами, в то время как маршруты с произвольными
     * точками назначения не подлежат долговременному хранению.»
     *
     * @return конфигурация Redis
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues();
    }


    @Bean
    public RedisCacheConfiguration portToPortCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(14))
                .disableCachingNullValues();
    }

    @Bean
    public RedisCacheConfiguration adHocCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(3))
                .disableCachingNullValues();
    }

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
