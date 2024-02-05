package com.github.renamrgb.infra.repositories.impl;

import com.github.renamrgb.application.exception.DomainException;
import com.github.renamrgb.domain.FeatureFlag;
import com.github.renamrgb.infra.entities.FeatureFlagEntity;
import com.github.renamrgb.infra.repositories.FeatureFlagRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

@ApplicationScoped
public class FeatureFlagRepositoryImpl implements FeatureFlagRepository {

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    public Uni<FeatureFlag> save(FeatureFlag featureFlag) {
        return hasFlag(featureFlag.module(), featureFlag.flagName(), featureFlag.sellerIdentifier())
                .flatMap(exists -> {
                    if (!exists) {
                        FeatureFlagEntity entity = featureFlag.toEntity();
                        this.persist(entity);
                        this.flush();
                        return Uni.createFrom().item(entity.toDomain());
                    } else {
                        return Uni.createFrom().failure(new DomainException("Feature flag with the same constraint already exists."));
                    }
                });
    }

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    public Uni<Boolean> existsByParameters(String flagName, String sellerIdentifier, String module) {
        return hasFlag(module, flagName, sellerIdentifier);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    public Uni<Boolean> deleteByParams(String flagName, String sellerIdentifier, String module) {
        final String query = "module = :module and flagName = :flagName and sellerIdentifier = :sellerIdentifier";
        final Parameters params = Parameters.with("module", module)
                .and("flagName", flagName)
                .and("sellerIdentifier", sellerIdentifier);

        return Uni.createFrom().item(() -> {
            FeatureFlagEntity entity = this.find(query, params).singleResultOptional()
                    .orElseThrow(() -> new NotFoundException("Feature flag not found with the specified parameters"));
            return this.deleteById(entity.id);
        });
    }

    private Uni<Boolean> hasFlag(final String module, final String flagName, final String sellerIdentifier) {
        final String query = "module = :module and flagName = :flagName and sellerIdentifier = :sellerIdentifier";
        final Parameters params = Parameters.with("module", module)
                .and("flagName", flagName)
                .and("sellerIdentifier", sellerIdentifier);

        return Uni.createFrom().item(() -> Optional.ofNullable(this.find(query, params)
                .firstResult()).isPresent());
    }
}
