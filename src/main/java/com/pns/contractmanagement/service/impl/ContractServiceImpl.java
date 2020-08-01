package com.pns.contractmanagement.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.repository.ContractRepository;

@Service
public class ContractServiceImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(ContractServiceImpl.class);

    @Autowired
    ContractRepository repository;

    public Contract addContract(final Contract contract) throws PnsException {
        if (!repository.findById(contract.getId()).isPresent()) {
            return repository.save(contract);
        }
        throw new PnsException("Contract is already present with same details", PnsError.DUPLICTE_RECORD);
    }

    public Contract modifyContract(final Contract contract) throws PnsException {
        getContractbyid(contract.getId());
        return repository.save(contract);
    }

    public Contract DeleteContractById(final long id) throws PnsException {
        final Contract deletedContract = getContractbyid(id);
        repository.deleteById(id);
        return deletedContract;
    }

    public Contract getContractbyid(final long id) throws PnsException {
        return repository.findById(id).orElseThrow(() -> new PnsException("Contract Not Found!!", PnsError.NOT_FOUND));
    }

    public List<Contract> getAllContract() {
        return repository.findAll();
    }
}
