package com.pns.contractmanagement.model.v1;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.EquipmentItem;

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
public class Contract {
    
    @Id
    private long id;
    private  Customer customer;
    private  EquipmentItem equipment;
    private  LocalDate startDate;
    private  LocalDate endDate;
    private  double basicAmount;
    private  double totalAmount;
    private  double gst;

}
