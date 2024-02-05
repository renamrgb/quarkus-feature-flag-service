package com.github.renamrgb.infra.repositories;

import com.github.renamrgb.domain.FeatureFlag;
import com.github.renamrgb.infra.entities.FeatureFlagEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;

public interface FeatureFlagRepository extends PanacheRepositoryBase<FeatureFlagEntity, Long> {
    Uni<FeatureFlag> save(final FeatureFlag featureFlag);
    Uni<Boolean> existsByParameters(String flagName, String sellerIdentifier, String module);
    Uni<Boolean> deleteByParams(String flagName, String sellerIdentifier, String module);
}
