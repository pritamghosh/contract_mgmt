/**
 * 
 */
package com.pns.contractmanagement.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.Nullable;

/**
 * @author Pritam Ghosh
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableLeaveRequestDetails.Builder.class)
public interface LeaveRequestDetails {
	String getId();

	LeaveType getType();

	float getNoOfDays();

	String getEmployeeId();

	String getPrimaryApprover();

	String getNote();

	LocalDate getFrom();

	LocalDate getTo();

	@Nullable
	String getApprovedBy();

	@Nullable
	LocalDateTime getApprovalDateTime();

	@Nullable
	String getApprovarNote();

	LeaveStatus getStatus();

	LocalDateTime getDateTimeOfApply();
	
	default boolean isCancelPossible() {
		return getStatus() == LeaveStatus.APPROVED && getTo().plusDays(30).isBefore(LocalDate.now());
	}
}
