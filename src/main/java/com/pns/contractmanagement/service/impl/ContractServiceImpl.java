package com.pns.contractmanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.ContractDao;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.ImmutableContract;

@Service
public class ContractServiceImpl {

    @Autowired
    ContractDao contractDao;

    @Autowired
    EquipmentServiceImpl equipmenyService;

    public Contract addContract(final Contract contract) throws PnsException {
        return map(contractDao.insert(map(contract)));
    }

    public Contract getContractbyId(final String id) throws PnsException {
        return map(
            contractDao.findById(id).orElseThrow(() -> new PnsException("Contract Not Found!!", PnsError.NOT_FOUND)));
    }

    public Contract modifyContract(final Contract contract) throws PnsException {
        contractDao.update(map(contract));
        return getContractbyId(contract.getId());
    }

    public Contract DeleteContractById(final String id) throws PnsException {
        final Contract deletedContract = getContractbyId(id);
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
            .build();
        // @formatter:on
        entity.setId(contract.getId());
        return entity;

    }
}
