package com.pns.contractmanagement.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableLeaveQuota.Builder.class)
public interface LeaveQuota {
LeaveType getType();
float getTotalNo();
float getReameningNo();
}
