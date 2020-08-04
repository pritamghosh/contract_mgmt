package com.pns.contractmanagement.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableEquipment.Builder.class)
public interface Equipment {
    @Nullable
    @JsonProperty("_id")
    String getId();

    String getModel();

    @Nullable
    String getDescription();

    @Nullable
    Long getCount();
}
