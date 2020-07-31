package com.pns.contractmanagement.model;

import java.time.LocalDate;

import org.immutables.value.Value;
@Value.Immutable
public interface Contract {
Customer getCustomer();

Equipment getEquipment();

LocalDate getStartDate();


LocalDate endStartDate();

double getBasicAmount();

double getTotalAmount();

double getGst();

}
