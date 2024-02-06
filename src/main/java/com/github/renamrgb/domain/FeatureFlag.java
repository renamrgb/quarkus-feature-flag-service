package com.github.renamrgb.domain;

import com.github.renamrgb.infra.entities.FeatureFlagEntity;
import jakarta.validation.constraints.NotBlank;

public record FeatureFlag(
        @NotBlank(message = "'flagName' cannot be null or empty")
        String flagName,

        @NotBlank(message = "'sellerIdentifier' cannot be null or empty")
        String sellerIdentifier,

        @NotBlank(message = "'module' cannot be null or empty")
        String module
) {

    public FeatureFlagEntity toEntity() {
        return new FeatureFlagEntity(flagName(), sellerIdentifier(), module());
    }
}
