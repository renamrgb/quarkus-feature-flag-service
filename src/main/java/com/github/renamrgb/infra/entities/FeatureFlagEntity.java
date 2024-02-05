package com.github.renamrgb.infra.entities;

import com.github.renamrgb.domain.FeatureFlag;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
public class FeatureFlagEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String flagName;
    public String sellerIdentifier;
    public String module;


    public FeatureFlagEntity() {
    }

    public FeatureFlagEntity(String flagName, String sellerIdentifier, String module) {
        this.flagName = flagName;
        this.sellerIdentifier = sellerIdentifier;
        this.module = module;
    }

    public FeatureFlag toDomain() {
        return new FeatureFlag(id, flagName, sellerIdentifier, module);
    }
}
