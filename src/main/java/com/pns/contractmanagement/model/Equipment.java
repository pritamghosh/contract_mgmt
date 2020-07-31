package com.pns.contractmanagement.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
//@JsonDeserialize(builder = Im)
public interface Equipment {
	long getId();

	String getName();
}
