package com.pns.contractmanagement.model;

import java.time.LocalDate;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableLeaveRequest.Builder.class)
public interface LeaveRequest {
	LeaveType getType();

	String getPrimaryApprover();

	String getNote();

	LocalDate getFrom();

	LocalDate getTo();

}
