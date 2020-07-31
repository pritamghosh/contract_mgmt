package com.pns.contractmanagement.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableCustomer.Builder.class)
public interface Customer {
	public long getId();

	public String getName();
}
