package com.pns.contractmanagement.model;

import java.time.LocalDate;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 *
 */
@Value.Immutable
@JsonDeserialize(builder = ImmutableContract.Builder.class)
public interface Contract {

    @Nullable
    @JsonProperty("_id")
    String getId();

    LocalDate getAmcStartDate();

    LocalDate getAmcEndDate();

    Customer getCustomer();

    EquipmentItem getEquipmentItem();

    double getAmcTotalAmount();

    double getAmcBasicAmount();

    double getAmcTax();

    @Nullable
    Double getAmcTaxAmount();

    String getBillingCycle();
    

    @Nullable
    String getNote();

    @Nullable
    LocalDate getContractDate();

    @Nullable
    String getProposalNo();
    
    @Nullable
    String getPoFileContentType();
    @Nullable
    byte[] getPoFileContent();
    @Nullable
    String getPoFileName();

}
