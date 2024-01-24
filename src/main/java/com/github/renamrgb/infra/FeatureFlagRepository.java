package com.github.renamrgb.infra;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeatureFlagRepository implements PanacheRepositoryBase<FeatureFlagEntity, Long> {
}
