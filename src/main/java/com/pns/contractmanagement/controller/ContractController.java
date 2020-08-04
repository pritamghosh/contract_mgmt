package com.pns.contractmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.service.impl.ContractServiceImpl;

@RestController
@RequestMapping("/contract")
public class ContractController {


    @Autowired
    ContractServiceImpl service;

    @PutMapping
    public Contract addContract(@RequestBody final Contract Contract) throws PnsException {
        return service.addContract(Contract);
    }

    @PostMapping
    public Contract modifyContract(@RequestBody final Contract Contract) throws PnsException {
        return service.modifyContract(Contract);
    }

    @DeleteMapping("/{id}")
    public Contract DeleteContract(@PathVariable("id") final String id) throws PnsException {
        return service.DeleteContractById(id);
    }

    @GetMapping("{id}")
    public Contract getContractById(@PathVariable("id") final String id) throws PnsException {
        return service.DeleteContractById(id);
    }

    @GetMapping
    public List<Contract> getAllContract() {
        return service.getAllContract();
    }
    
    @GetMapping("/customer/{id}")
    public List<Contract> getContractsByCustomerId(@PathVariable("id") final String customerId) {
        return service.getContractsByCustomerId(customerId);
    }
    
    @GetMapping("/equipment/{id}")
    public List<Contract> getContractsByEquipmentrId(@PathVariable("id") final String equipmentId) {
        return service.getContractsByEquipmentrId(equipmentId);
    }
    
    
    
    @GetMapping("/search")
    public List<Contract> searchContractByQuery(@RequestParam("query") final String query) {
        return service.searchContractByQuery(query);
    }
}
