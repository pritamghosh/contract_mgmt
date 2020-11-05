package com.pns.contractmanagement.model;

import java.time.LocalDate;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableLeaveQuotaDetails.Builder.class)
public interface LeaveQuotaDetails {
	List<LeaveQuota> getDetails();

	String getEmployeeID();

	long getYear();

	@Value.Immutable
	@JsonDeserialize(builder = ImmutableLeaveQuota.Builder.class)
	public interface LeaveQuota {
		LeaveType getType();

		float getTotalLeaves();

		float getReameningLeaves();
		
		float getUsedLeaves();
		
		float getApprovalPendingLeaves();
		
		LocalDate getDeductableFrom();
		
		LocalDate getDeductableTo();
	}
}
