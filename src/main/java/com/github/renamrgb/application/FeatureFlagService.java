package com.github.renamrgb.application;

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

    public Uni<FeatureFlag> create(final FeatureFlag featureFlag) {
        return repository.save(featureFlag);
    }

    @Transactional
    public Uni<Boolean> existsByParameters(String flagName, String sellerIdentifier, String module) {
        return repository.existsByParameters(flagName, sellerIdentifier, module);
    }

    @Transactional
    public Uni<Boolean> deleteByParams(String flagName, String sellerIdentifier, String module) {
        return repository.deleteByParams(flagName, sellerIdentifier, module);
    }

}
