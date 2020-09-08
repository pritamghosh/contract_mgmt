package com.pns.contractmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pns.contractmanagement.model.LeaveRequest.Status;
import com.pns.contractmanagement.model.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestEntity {
    private LeaveType type;
    private float noOfDays;
    private String employeeId;
    private String getprimaryApprover;
    private String secondaryApprover;
    private String note;
    private LocalDate from;
    private LocalDate to;
    private String approvedBy;
    private LocalDateTime approvalDateTime;
    private String approvarNote;
    private Status status;
    private LocalDateTime requestDateTime;

}
