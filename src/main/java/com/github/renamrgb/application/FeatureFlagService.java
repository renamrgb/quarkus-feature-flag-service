package com.github.renamrgb.application;

import com.github.renamrgb.application.exception.DomainException;
import com.github.renamrgb.domain.FeatureFlag;
import com.github.renamrgb.infra.FeatureFlagEntity;
import com.github.renamrgb.infra.FeatureFlagRepository;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;

@ApplicationScoped
public class FeatureFlagService {

    @Inject
    FeatureFlagRepository repository;

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<FeatureFlag> create(final FeatureFlag featureFlag) {
        return hasFlag(featureFlag.module(), featureFlag.flagName(), featureFlag.sellerIdentifier())
                .flatMap(exists -> {
                    if (!exists) {
                        FeatureFlagEntity entity = featureFlag.toEntity();
                        repository.persist(entity);
                        repository.flush();
                        return Uni.createFrom().item(entity.toDomain());
                    } else {
                        return Uni.createFrom().failure(new DomainException("Feature flag with the same constraint already exists."));
                    }
                });
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<Boolean> existsById(final Long id) {
        final String query = "id = :id";
        final Parameters params = Parameters.with("id", id);
        return exists(query, params);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<Boolean> existsByParameters(String flagName, String sellerIdentifier, String module) {
        return hasFlag(module, flagName, sellerIdentifier);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<Boolean> deleteById(Long id) {
        return Uni.createFrom().optional(() -> repository.findByIdOptional(id))
                .onItem().transformToUni(entity -> Uni.createFrom().item(repository.deleteById(entity.id)));
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Uni<Boolean> deleteByParams(String flagName, String sellerIdentifier, String module) {
        final String query = "module = :module and flagName = :flagName and sellerIdentifier = :sellerIdentifier";
        final Parameters params = Parameters.with("module", module)
                .and("flagName", flagName)
                .and("sellerIdentifier", sellerIdentifier);

        return Uni.createFrom().item(() -> {
            FeatureFlagEntity entity = repository.find(query, params).singleResultOptional()
                    .orElseThrow(() -> new NotFoundException("Feature flag not found with the specified parameters"));
            return repository.deleteById(entity.id);
        });
    }

    private Uni<Boolean> hasFlag(final String module, final String flagName, final String sellerIdentifier) {
        final String query = "module = :module and flagName = :flagName and sellerIdentifier = :sellerIdentifier";
        final Parameters params = Parameters.with("module", module)
                .and("flagName", flagName)
                .and("sellerIdentifier", sellerIdentifier);

        return exists(query, params);
    }

    private Uni<Boolean> exists(final String query, final Parameters params) {
        return Uni.createFrom().item(() -> Optional.ofNullable(repository.find(query, params)
                .firstResult()).isPresent());
    }
}
