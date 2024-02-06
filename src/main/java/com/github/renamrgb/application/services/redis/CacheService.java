package com.github.renamrgb.application.services.redis;

public interface CacheService {

    void save(String key);

    void save(String key, Boolean isActive);

    boolean exists(String key);

    void delete(String key);

    default String getKey(String ... key) {
        return String.join(":", key);
    }
}
