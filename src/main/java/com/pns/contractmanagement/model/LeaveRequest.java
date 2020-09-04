package com.pns.contractmanagement.model;

import java.time.LocalDate;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.lang.Nullable;

/**
 *
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableLeaveRequest.Builder.class)
public interface LeaveRequest {
    LeaveType getType();
    float getNoOfDays();
    String getEmployeeId();
    String getprimaryApprover();
    @Nullable
    String getSecondaryApprover();
    String getNote();
    LocalDate getFrom();
    LocalDate getTo();
    
    public static enum Status{
        PENDING,APPROVED,REJECTED;
    }
}
