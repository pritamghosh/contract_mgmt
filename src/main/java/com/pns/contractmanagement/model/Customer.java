package com.pns.contractmanagement.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableCustomer.Builder.class)
public interface Customer  {

    @Nullable
    @JsonProperty("_id")
    String getId();
    
    String getName();

    String getRegion();
    
    String getAddress();
    @Nullable
    String getGstinNo();
    @Nullable
    String getPan();

}
