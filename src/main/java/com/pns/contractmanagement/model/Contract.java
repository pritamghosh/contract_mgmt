package com.pns.contractmanagement.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@Getter
@Data
@NoArgsConstructor
@JsonDeserialize(builder = Contract.ContractBuilder.class)
public class Contract {
    
    @Id
    private long id;
    private  Customer customer;
    private  Equipment equipment;
    private  LocalDate startDate;
    private  LocalDate endDate;
    private  double basicAmount;
    private  double totalAmount;
    private  double gst;

}
