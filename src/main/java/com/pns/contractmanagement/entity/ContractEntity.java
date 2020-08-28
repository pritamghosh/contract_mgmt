package com.pns.contractmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;

import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.EquipmentItem;

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
public class ContractEntity extends BaseMongoEntity {

    private Customer customer;
    private EquipmentItem equipmentItem;
    private LocalDate amcStartDate;
    private LocalDate amcEndDate;
    private double amcBasicAmount;
    private double amcTotalAmount;
    private double amcTax;
    private double amcTaxAmount;
    private String billingCycle;
    private String note;
    private ObjectId customerOid;
    private ObjectId equipmnetOid;
    private LocalDate contractDate;
    private String proposalNo;
    private byte[] poFileContent;
    private String poFileContentType;
    private String poFileName;
    private String status;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private ContractEntity oldContract;
}
