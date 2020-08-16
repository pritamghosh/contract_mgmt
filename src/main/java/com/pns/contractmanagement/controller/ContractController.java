package com.pns.contractmanagement.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Range;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.service.impl.ContractServiceImpl;

@RestController
@RequestMapping("/contract")
public class ContractController {
	Logger logger = LoggerFactory.getLogger(ContractController.class);
	

    @Autowired
    private ContractServiceImpl service;

    @PutMapping
    public ResponseEntity<byte[]> addContract(@RequestBody final Contract contract) throws PnsException {
    	
        final Report contractReport = service.addContract(contract);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contractReport.getContentType());
        String filename = contractReport.getFileName();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity(contractReport.getContent(), headers, HttpStatus.OK);
        return response;
        // return service.addContract(contract);
    }

    @PostMapping
    public Contract modifyContract(@RequestBody final Contract contract) throws PnsException {
        return service.modifyContract(contract);
    }

    @DeleteMapping("/{id}")
    public Contract DeleteContract(@PathVariable("id") final String id) throws PnsException {
        return service.deleteContractById(id);
    }

    @GetMapping("{id}")
    public Contract getContractById(@PathVariable("id") final String id) throws PnsException {
        return service.getContractById(id);
    }
    
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getContractPdfById(@PathVariable("id") final String id) throws PnsException {
        Report contractReport = service.getContractReportById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contractReport.getContentType());
        String filename = contractReport.getFileName();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity(contractReport.getContent(), headers, HttpStatus.OK);
        return response;
        
    }

    @GetMapping(params = { "!form","!to","!create" })
    public List<Contract> getAllContract() {
    	logger.debug("debug");
    	logger.info("info");
    	logger.error("error");
    	logger.trace("trace");
        return service.getAllContract();
    }
    
    @GetMapping(params = { "form","to","!create" })
    public List<Contract> getContractsByAMCDateRange(@RequestParam("form") String form,@RequestParam("to") String to) {
		return service.getContractByAmcDateRange(Range.closed(LocalDate.parse(form),LocalDate.parse(to)));
    }
    
    @GetMapping(params = { "form","to","create" })
    public List<Contract> getContractsByCreationDate(@RequestParam("form") String form,@RequestParam("to") String to) {
        return service.getContractByCreationDateRange(Range.closed(LocalDate.parse(form),LocalDate.parse(to)));
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
