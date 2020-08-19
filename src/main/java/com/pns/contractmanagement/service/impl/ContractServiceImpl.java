package com.pns.contractmanagement.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.pns.contractmanagement.dao.ContractDao;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableContract;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;
import com.pns.contractmanagement.model.ImmutableSearchResponse;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.model.SearchResponse;

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
		final Customer customerbyid = customerService.getCustomerbyid(contract.getCustomer().getId());
		EquipmentItem equipmentItem = null;
		if (contract.getEquipmentItem().getId() == null) {
			final Equipment equipmentbyid = equipmentService
					.getEquipmentById(contract.getEquipmentItem().getEquipment().getId());
			equipmentItem = equipmentService.addEquipmentItem(ImmutableEquipmentItem.builder().equipment(equipmentbyid)
					.serialNumber(contract.getEquipmentItem().getSerialNumber()).build());
		} else {
			equipmentItem = equipmentService.getEquipmentItemById(contract.getEquipmentItem().getId());
		}

		final SequenceEntity proposalSequence = contractDao.findAndUpdateSequece();
		final LocalDate currentDate = proposalSequence.getDate();
		final String proposalNo = new StringBuilder("PNS-").append(currentDate.getDayOfMonth()).append("/")
				.append(currentDate.getMonthValue()).append("/").append(currentDate.getYear()).append("/")
				.append(proposalSequence.getSequence()).toString();
		final double amcBasicAmount = roundUp(contract.getAmcBasicAmount());
		final double amcTaxAmount = roundUp(amcBasicAmount * tax / 100);
		final double amcTotalAmount = roundUp(amcBasicAmount + amcTaxAmount);
		final Contract insertedContract = map(
				contractDao.insert(map(ImmutableContract.builder().from(contract).amcBasicAmount(amcBasicAmount)
						.amcTax(tax).amcTaxAmount(amcTaxAmount).amcTotalAmount(amcTotalAmount).customer(customerbyid)
						.equipmentItem(equipmentItem).proposalNo(proposalNo).contractDate(currentDate).build())));
		return invoiceHelper.generateInvoice(insertedContract);
	}

	public Report getContractReportById(final String id) throws PnsException {
		return invoiceHelper.generateInvoice(getContractById(id));
	}

	public Contract getContractById(final String id) throws PnsException {
		return map(contractDao.findById(id)
				.orElseThrow(() -> new PnsException("Contract Not Found!!", PnsError.NOT_FOUND)));
	}

	public Contract modifyContract(final Contract contract) throws PnsException {
		final Customer customerbyid = customerService.getCustomerbyid(contract.getCustomer().getId());
		EquipmentItem equipmentItem = null;
		if (contract.getEquipmentItem().getId() == null) {
			final Equipment equipmentbyid = equipmentService
					.getEquipmentById(contract.getEquipmentItem().getEquipment().getId());
			equipmentItem = equipmentService.addEquipmentItem(ImmutableEquipmentItem.builder().equipment(equipmentbyid)
					.serialNumber(contract.getEquipmentItem().getSerialNumber()).build());
		} else {
			equipmentItem = equipmentService.getEquipmentItemById(contract.getEquipmentItem().getId());
		}
		final double amcBasicAmount = roundUp(contract.getAmcBasicAmount());
		final double amcTaxAmount = roundUp(amcBasicAmount * tax / 100);
		final double amcTotalAmount = roundUp(amcBasicAmount + amcTaxAmount);
		contractDao.update(map(ImmutableContract.builder().from(contract).amcTax(tax).amcTaxAmount(amcTaxAmount)
				.amcTotalAmount(amcTotalAmount).customer(customerbyid).amcBasicAmount(amcBasicAmount)
				.equipmentItem(equipmentItem).contractDate(LocalDate.now()).build()));
		return getContractById(contract.getId());
	}

	private double roundUp(final double amount) {
		return Math.round(amount * 100.0) / 100.0;
	}

	public Contract deleteContractById(final String id) throws PnsException {
		final Contract deletedContract = getContractById(id);
		contractDao.deleteById(id);
		return deletedContract;
	}

	public SearchResponse<Contract> getContractByAmcDateRange(final Range<LocalDate> dateRange, final int page) {
		return ImmutableSearchResponse.<Contract>builder()
				.result(map(contractDao.findContractByAmcDateRange(dateRange, page)))
				.pageCount(contractDao.countDocumnetsByAmcDateRange(dateRange)).build();
	}

	public SearchResponse<Contract> getContractByCreationDateRange(final Range<LocalDate> dateRange, final int page) {
		return ImmutableSearchResponse.<Contract>builder()
				.result(map(contractDao.findContractByCreationDateRange(dateRange, page)))
				.pageCount(contractDao.countDocumnetsByCreationDateRange(dateRange)).build();
	}

	public SearchResponse<Contract> getAllContract(final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findAll(page)))
				.pageCount(contractDao.countAllDocumnets()).build();
	}

	public SearchResponse<Contract> getContractsByCustomerId(final String customerId, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findByCustomerId(customerId, page)))
				.pageCount(contractDao.countDocumnetsByCustomerId(customerId)).build();
	}

	public SearchResponse<Contract> getContractsByEquipmentrId(final String equipmentId, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findByEquipmentId(equipmentId, page)))
				.pageCount(contractDao.countDocumnetsByEquipmentId(equipmentId)).build();
	}

	public SearchResponse<Contract> searchContractByQuery(final String query, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.searchByQuery(query, page)))
				.pageCount(contractDao.countDocumnetsByQuery(query)).build();
	}

	private List<Contract> map(final List<ContractEntity> list) {
		return list.stream().map(e -> map(e)).collect(Collectors.toList());
	}

	private Contract map(final ContractEntity entity) {
		// @formatter:off
		return ImmutableContract.builder().amcBasicAmount(entity.getAmcBasicAmount()).amcEndDate(entity.getAmcEndDate())
				.amcStartDate(entity.getAmcStartDate()).amcTax(entity.getAmcTax())
				.amcTotalAmount(entity.getAmcTotalAmount()).billingCycle(entity.getBillingCycle())
				.customer(entity.getCustomer()).equipmentItem(entity.getEquipmentItem()).note(entity.getNote())
				.id(entity.getId()).contractDate(entity.getContractDate()).proposalNo(entity.getProposalNo())
				.amcTaxAmount(entity.getAmcTaxAmount()).build();
		// @formatter:on

	}

	private ContractEntity map(final Contract contract) {
		// @formatter:off
		final ContractEntity entity = ContractEntity.builder().amcBasicAmount(contract.getAmcBasicAmount())
				.amcEndDate(contract.getAmcEndDate()).amcStartDate(contract.getAmcStartDate())
				.amcTax(contract.getAmcTax()).amcTotalAmount(contract.getAmcTotalAmount())
				.billingCycle(contract.getBillingCycle()).customer(contract.getCustomer())
				.equipmentItem(contract.getEquipmentItem()).note(contract.getNote())
				.contractDate(contract.getContractDate()).proposalNo(contract.getProposalNo())
				.amcTaxAmount(contract.getAmcTaxAmount() != null ? contract.getAmcTaxAmount() : 0).build();
		// @formatter:on
		entity.setId(contract.getId());
		return entity;

	}

}
