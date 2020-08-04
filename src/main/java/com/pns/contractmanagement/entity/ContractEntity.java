package com.pns.contractmanagement.entity;

import java.time.LocalDate;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableCustomer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractEntity extends AbstractMongoEntity {

    private Customer customer;
    private EquipmentItem equipmentItem;
    private LocalDate amcStartDate;
    private LocalDate amcEndDate;
    private double amcBasicAmount;
    private double amcTotalAmount;
    private double amcTax;
    private String billingCycle;
    private String note;
    private ObjectId customerOid;
    private ObjectId equipmnetOid;

}
