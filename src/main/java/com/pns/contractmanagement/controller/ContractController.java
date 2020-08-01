package com.pns.contractmanagement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.service.impl.ContractServiceImpl;

@RestController
@RequestMapping("/contract")
public class ContractController {

    private final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);

    @Autowired
    ContractServiceImpl service;

    @PutMapping
    public Contract addContract(final Contract Contract) throws PnsException {
        return service.addContract(Contract);
    }

    @PostMapping
    public Contract modifyContract(final Contract Contract) throws PnsException {
        return service.modifyContract(Contract);
    }

    @DeleteMapping("/{id}")
    public Contract DeleteContract(@PathVariable("id") final long id) throws PnsException {
        return service.DeleteContractById(id);
    }

    @GetMapping("{id}")
    public Contract getContractbyid(@PathVariable("id") final long id) throws PnsException {
        return service.DeleteContractById(id);
    }

    @GetMapping

    public List<Contract> getAllContractbyid() {
        return service.getAllContract();
    }
}
