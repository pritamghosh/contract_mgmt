package com.pns.contractmanagement.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.ContractDao;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableContract;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;
import com.pns.contractmanagement.model.Report;

@Service
public class ContractServiceImpl {

    @Value("${app.gst.value:18}")
    private double tax;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private EquipmentServiceImpl equipmentService;

    @Autowired
    private ContractInvoiceHelperImpl invoiceHelper;

    public Report addContract(final Contract contract) throws PnsException {
        Customer customerbyid = customerService.getCustomerbyid(contract.getCustomer().getId());
        EquipmentItem equipmentItem = null;
        if (contract.getEquipmentItem().getId() == null) {
            Equipment equipmentbyid = equipmentService
                .getEquipmentById(contract.getEquipmentItem().getEquipment().getId());
            equipmentItem = equipmentService.addEquipmentItem(ImmutableEquipmentItem.builder().equipment(equipmentbyid)
                .serialNumber(contract.getEquipmentItem().getSerialNumber()).build());
        } else {
            equipmentItem = equipmentService.getEquipmentItemById(contract.getEquipmentItem().getId());
        }
        LocalDateTime now = LocalDateTime.now();
        String proposalNo = new StringBuilder("PNS-").append(now.getDayOfMonth()).append("/")
            .append(now.getMonthValue()).append("/").append(now.getYear()).toString();
        double amcTaxAmount = contract.getAmcBasicAmount() * tax / 100;
        final Contract insertedContract = map(
            contractDao.insert(map(ImmutableContract.builder().from(contract).amcTax(tax).amcTaxAmount(amcTaxAmount)
                .amcTotalAmount(contract.getAmcBasicAmount() + amcTaxAmount).customer(customerbyid)
                .equipmentItem(equipmentItem).proposalNo(proposalNo).contractDate(LocalDate.now()).build())));
        return invoiceHelper.generateInvoice(insertedContract);
    }

    public Report getContractReportById(final String id) throws PnsException {
        return invoiceHelper.generateInvoice(getContractById(id));
    }

    public Contract getContractById(final String id) throws PnsException {
        return map(
            contractDao.findById(id).orElseThrow(() -> new PnsException("Contract Not Found!!", PnsError.NOT_FOUND)));
    }

    public Contract modifyContract(final Contract contract) throws PnsException {
        Customer customerbyid = customerService.getCustomerbyid(contract.getCustomer().getId());
        EquipmentItem equipmentItem = null;
        if (contract.getEquipmentItem().getId() == null) {
            Equipment equipmentbyid = equipmentService
                .getEquipmentById(contract.getEquipmentItem().getEquipment().getId());
            equipmentItem = equipmentService.addEquipmentItem(ImmutableEquipmentItem.builder().equipment(equipmentbyid)
                .serialNumber(contract.getEquipmentItem().getSerialNumber()).build());
        } else {
            equipmentItem = equipmentService.getEquipmentItemById(contract.getEquipmentItem().getId());
        }
        double amcTaxAmount = contract.getAmcBasicAmount() * tax / 100;
        contractDao.update(map(ImmutableContract.builder().from(contract).amcTax(tax).amcTaxAmount(amcTaxAmount)
            .amcTotalAmount(contract.getAmcBasicAmount() + amcTaxAmount).customer(customerbyid)
            .equipmentItem(equipmentItem).contractDate(LocalDate.now()).build()));
        return getContractById(contract.getId());
    }

    public Contract deleteContractById(final String id) throws PnsException {
        final Contract deletedContract = getContractById(id);
        contractDao.deleteById(id);
        return deletedContract;
    }

    public List<Contract> getAllContract() {
        return map(contractDao.findAll());
    }

    public List<Contract> getContractsByCustomerId(final String customerId) {
        return map(contractDao.findByCustomerId(customerId));
    }

    public List<Contract> getContractsByEquipmentrId(final String equipmentId) {
        return map(contractDao.findByEquipmentId(equipmentId));
    }

    public List<Contract> searchContractByQuery(final String query) {
        return map(contractDao.searchByQuery(query));
    }

    private List<Contract> map(final List<ContractEntity> list) {
        return list.stream().map(e -> map(e)).collect(Collectors.toList());
    }

    private Contract map(final ContractEntity entity) {
        // @formatter:off
        return ImmutableContract.builder()
            .amcBasicAmount(entity.getAmcBasicAmount())
            .amcEndDate(entity.getAmcEndDate())
            .amcStartDate(entity.getAmcStartDate())
            .amcTax(entity.getAmcTax())
            .amcTotalAmount(entity.getAmcTotalAmount())
            .billingCycle(entity.getBillingCycle())
            .customer(entity.getCustomer())
            .equipmentItem(entity.getEquipmentItem())
            .note(entity.getNote())
            .id(entity.getId())
            .contractDate(entity.getContractDate())
            .proposalNo(entity.getProposalNo())
            .amcTaxAmount(entity.getAmcTaxAmount())
            .build();
        // @formatter:on

    }

    private ContractEntity map(final Contract contract) {
        // @formatter:off
        final ContractEntity entity = ContractEntity.builder()
            .amcBasicAmount(contract.getAmcBasicAmount())
            .amcEndDate(contract.getAmcEndDate())
            .amcStartDate(contract.getAmcStartDate())
            .amcTax(contract.getAmcTax())
            .amcTotalAmount(contract.getAmcTotalAmount())
            .billingCycle(contract.getBillingCycle())
            .customer(contract.getCustomer())
            .equipmentItem(contract.getEquipmentItem())
            .note(contract.getNote())
            .contractDate(contract.getContractDate())
            .proposalNo(contract.getProposalNo())
            .amcTaxAmount(contract.getAmcTaxAmount()!=null?contract.getAmcTaxAmount():0)
            .build();
        // @formatter:on
        entity.setId(contract.getId());
        return entity;

    }
}
