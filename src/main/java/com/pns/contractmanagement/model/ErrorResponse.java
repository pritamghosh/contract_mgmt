package com.pns.contractmanagement.model;

import org.immutables.value.Value;

@Value.Immutable
public interface ErrorResponse {
	String getMessage();
}
