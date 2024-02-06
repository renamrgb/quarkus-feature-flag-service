package com.github.renamrgb.infra.cache;

import com.github.renamrgb.application.services.redis.CacheService;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisCache implements CacheService {

    private static final long TTL = 60 * 60 * 24 * 2;

    private final ValueCommands<String, Boolean> cache;

    public RedisCache(RedisDataSource redisDataSource) {
        cache = redisDataSource.value(Boolean.class);
    }

    @Override
    public void save(final String key) {
        save(key, true);
    }

    @Override
    public void save(String key, Boolean isActive) {
        cache.setex(key, TTL, isActive);
    }

    @Override
    public boolean exists(final String key) {
        try {
            return cache.get(key);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void delete(final String key) {
        cache.getdel(key);
    }
}
