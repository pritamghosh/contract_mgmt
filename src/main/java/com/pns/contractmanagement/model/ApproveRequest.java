package com.pns.contractmanagement.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableApproveRequest.Builder.class)
public interface ApproveRequest {
	@JsonProperty("_id")
	String getId();

	String getComment();

	boolean getAction();
}
