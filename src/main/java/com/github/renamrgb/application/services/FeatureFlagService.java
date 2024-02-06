package com.github.renamrgb.application.services;

import com.github.renamrgb.application.services.redis.CacheService;
import com.github.renamrgb.domain.FeatureFlag;
import com.github.renamrgb.infra.repositories.FeatureFlagRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FeatureFlagService {

    @Inject
    FeatureFlagRepository repository;

    @Inject
    CacheService cacheService;

    @Transactional
    public Uni<Void> create(final FeatureFlag featureFlag) {
        return repository.save(featureFlag)
                .onItem().ignore()
                .andContinueWithNull()
                .onItem().transformToUni(ignored -> {
                    cacheService.save(getCacheKey(featureFlag));
                    return Uni.createFrom().nullItem();
                });
    }

    @Transactional
    public Uni<Boolean> existsByParameters(String flagName, String sellerIdentifier, String module) {
        final String cacheKey = getCacheKey(module, flagName, sellerIdentifier);
        boolean existInCache = cacheService.exists(cacheKey);
        if (existInCache) {
            return Uni.createFrom().item(existInCache);
        } else {
            return repository.existsByParameters(flagName, sellerIdentifier, module)
                    .onItem().transformToUni(existsInModule -> {
                        cacheService.save(cacheKey, existsInModule);
                        return Uni.createFrom().item(existsInModule);
                    });
        }
    }

    @Transactional
    public Uni<Boolean> deleteByParams(String flagName, String sellerIdentifier, String module) {
        cacheService.delete(getCacheKey(module, flagName, sellerIdentifier));
        return repository.deleteByParams(flagName, sellerIdentifier, module);
    }

    private String getCacheKey(final FeatureFlag featureFlag) {
        return getCacheKey(featureFlag.module(), featureFlag.flagName(), featureFlag.sellerIdentifier());
    }

    private String getCacheKey(final String module, final String flagName, final String sellerIdentifier) {
        return cacheService.getKey(module, flagName, sellerIdentifier);
    }

}
