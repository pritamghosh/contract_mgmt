package com.pns.contractmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pns.contractmanagement.model.LeaveStatus;
import com.pns.contractmanagement.model.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class LeaveRequestEntity extends AbstractMongoEntity{
	private LeaveType type;
	private float noOfDays;
	private String employeeId;
	private String primaryApprover;
	private String employeeName;
	private String note;
	private LocalDate from;
	private LocalDate to;
	private String approvedOrRejectedBy;
	private LocalDateTime approvalOrRejectionDateTime;
	private String approvarNote;
	private LeaveStatus status;
	private LocalDateTime dateTimeOfApply;

}
