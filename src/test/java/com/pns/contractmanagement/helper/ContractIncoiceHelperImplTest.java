package com.pns.contractmanagement.helper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.ImmutableContract;
import com.pns.contractmanagement.model.ImmutableCustomer;
import com.pns.contractmanagement.model.ImmutableEquipment;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class ContractIncoiceHelperImplTest {

    @InjectMocks
    ContractInvoiceHelperImpl helper ;

    @Test
    void jasperTest() {
        // @formatter:off
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(helper, "dateFormat", "dd-MM-yyyy");
        final ImmutableCustomer customer = ImmutableCustomer.builder()
        .id("")
        .name("STAR SPORTS")
        .region("KOLKATA")
        .gstinNo("19AFYPA5383F1Z0")
        .address("429, GLOBAL VIILLAGE\nKOLKATA 700059")
        .pan("BIWXC3378S")
        .build();

        Equipment equipment = ImmutableEquipment.builder()
            .model("BX 321")
            .description("EPABX SYSTEM WITH ACCESSORIES")
            .build();
        final ImmutableContract contract = ImmutableContract.builder()
        .amcBasicAmount(10300)
        .amcEndDate(LocalDate.of(2021, 1, 20))
        .amcStartDate(LocalDate.of(2020, 1, 19))
        .amcTax(18)
        .amcTotalAmount(12000)
        .amcTaxAmount(1700.00)
        .billingCycle("Half Yearly")
        .customer(customer)
        .equipmentItem(ImmutableEquipmentItem.builder()
            .serialNumber("SN123457")
            .equipment(equipment )
            .build()
            )
        .note("")
        .id("5f2a93e5d380c063e8201e8c")
        .contractDate(LocalDate.now())
        .proposalNo("PNS-22/7/ss/1")
        .build();
        
// @formatter:on
        assertNotNull(helper.generateInvoice(contract));
    }
    
}
