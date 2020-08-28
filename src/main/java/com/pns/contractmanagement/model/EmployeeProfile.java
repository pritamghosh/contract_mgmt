package com.pns.contractmanagement.model;

import java.time.LocalDate;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableEmployeeProfile.Builder.class)
public interface EmployeeProfile {

    @Nullable
    @JsonProperty("_id")
    String getId();

    String getEmployeeId();

    String getFirstName();

    String getFamilyName();

    @Nullable
    String getMiddleName();

    @Nullable
    String getAddress();

    String getDesignation();

    String getBaseLocation();

    LocalDate getDateOfBirth();

    LocalDate getDateOfJoining();

    String getWorkEmail();

    String getMobileNo();

    String getGender();

    @Nullable
    String getWorkContactNo();

    @Nullable
    String getBloodGroup();
    
    @Nullable byte[] getImage();

}
