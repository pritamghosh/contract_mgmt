package com.pns.contractmanagement.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableEquipmentItem.Builder.class)
public interface EquipmentItem {

    @Nullable
    @JsonProperty("_id")
    String getId();

    Equipment getEquipment();

    String getSerialNumber();

}
