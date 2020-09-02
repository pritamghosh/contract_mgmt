package com.pns.contractmanagement.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableManager.Builder.class)
public interface Manager {
	@JsonProperty("_id")
	String getId();

	String getName();
	
	String getEmployeeId();

}
