package com.pns.contractmanagement.service;

import java.time.LocalDate;

import com.google.common.collect.Range;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.model.SearchResponse;

/**
 *
 */
public interface ContractService {

    Report addContract(Contract contract);

    Report getContractReportById(String id);

    Contract getContractById(String id);

    Contract modifyContract(Contract contract);

    Contract deleteContractById(String id);

    Contract getContractByProposalNo(String proposalNo);

    SearchResponse<Contract> getContractByAmcStartDateRange(Range<LocalDate> dateRange, int page);

    SearchResponse<Contract> getContractcByCreationDateRange(Range<LocalDate> dateRange, int page);

    SearchResponse<Contract> getAllContract(int page);

    SearchResponse<Contract> getContractsByCustomerId(String customerId, int page);

    SearchResponse<Contract> getContractsByEquipmentrId(String equipmentId, int page);

    SearchResponse<Contract> searchContractByQuery(String query, int page);

}
