package com.pns.contractmanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Range;
import com.pns.contractmanagement.model.ApproveRequest;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.ContractService;

@RestController
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    private ContractService service;

    @PutMapping
    @PreAuthorize("hasRole('create')")
    public Contract addContract(@RequestBody final Contract contract) {
        return service.addContract(contract);
    }

    @PostMapping
    @PreAuthorize("hasRole('update')")
    public Contract modifyContract(@RequestBody final Contract contract) {
        return service.modifyContract(contract);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('delete')")
    public Contract deleteContract(@PathVariable("id") final String id) {
        return service.deleteContractById(id);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('read')")
    public Contract getContractById(@PathVariable("id") final String id) {
        return service.getContractById(id);
    }

    @GetMapping("/pdf/{id}")
    @PreAuthorize("hasRole('read')")
    public ResponseEntity<byte[]> getContractPdfById(@PathVariable("id") final String id) {
        final Report contractReport = service.getContractReportById(id);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contractReport.getContentType());
        final String filename = contractReport.getFileName();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok().headers(headers).body(contractReport.getContent());

    }

    @GetMapping(params = { "!form", "!to", "!create", "!proposalNo" })
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getAllContract(@RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getAllContract(page);
    }

    @GetMapping(params = { "form", "to", "!create", "!proposalNo" })
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getContractsByAmcStartDateRange(@RequestParam("form") final String form,
        @RequestParam("to") final String to, @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getContractByAmcStartDateRange(Range.closed(LocalDate.parse(form), LocalDate.parse(to)), page);
    }

    @GetMapping(params = { "form", "to", "create", "!proposalNo" })
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getContractsByCreationDate(@RequestParam("form") final String form,
        @RequestParam("to") final String to, @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getContractcByCreationDateRange(Range.closed(LocalDate.parse(form), LocalDate.parse(to)), page);
    }

    @GetMapping(params = { "!form", "!to", "!create", "proposalNo" })
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getContractByProposalNo(@RequestParam("proposalNo") final String proposalNo) {
        return service.getContractByProposalNo(proposalNo);
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getContractsByCustomerId(@PathVariable("id") final String customerId,
        @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getContractsByCustomerId(customerId, page);
    }

    @GetMapping("/equipment/{id}")
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> getContractsByEquipmentrId(@PathVariable("id") final String equipmentId,
        @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getContractsByEquipmentrId(equipmentId, page);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('read')")
    public SearchResponse<Contract> searchContractByQuery(@RequestParam("query") final String query,
        @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.searchContractByQuery(query, page);
    }
    
    @GetMapping("/approve")
    @PreAuthorize("hasRole('contract_approver')")
    public SearchResponse<List<Contract>> getContractsForApproval(
        @RequestParam(value = "page", defaultValue = "1") final int page) {
        return service.getContractsForApproval(page);
    }
    @PatchMapping("/approve")
    @PreAuthorize("hasRole('contract_approver')")
	public Contract approveContract(@RequestBody final ApproveRequest approveRequest) {
		return service.approveContract(approveRequest);
	}
    
}
