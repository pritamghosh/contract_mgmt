package com.pns.contractmanagement.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.pns.contractmanagement.controller.RoleConstants;
import com.pns.contractmanagement.dao.impl.ContractDaoImpl;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;
import com.pns.contractmanagement.model.Contract;
import com.pns.contractmanagement.model.Contract.Status;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableContract;
import com.pns.contractmanagement.model.ImmutableContract.Builder;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;
import com.pns.contractmanagement.model.ImmutableSearchResponse;
import com.pns.contractmanagement.model.Report;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.ContractService;
import com.pns.contractmanagement.util.ServiceUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContractServiceImpl implements ContractService {

	@Value("${app.gst.value:18}")
	private double tax;

	@Autowired
	private ContractDaoImpl contractDao;

	@Autowired
	private CustomerServiceImpl customerService;

	@Autowired
	private EquipmentServiceImpl equipmentService;

	@Autowired
	private ContractInvoiceHelperImpl invoiceHelper;

	/** {@inheritDoc} */
	@Override
	public Contract addContract(final Contract contract) {
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
		final ContractEntity contractEntityToBeInserted = map(ImmutableContract.builder().from(contract)
				.amcBasicAmount(amcBasicAmount).amcTax(tax).amcTaxAmount(amcTaxAmount).amcTotalAmount(amcTotalAmount)
				.customer(customerbyid).equipmentItem(equipmentItem).proposalNo(proposalNo).contractDate(currentDate)
				.status(Status.PENDING).build());
		if (Status.APPROVED.name().equals(contractEntityToBeInserted.getStatus())) {
			contractEntityToBeInserted.setApprovedBy(ServiceUtil.getUsernameFromContext());
			contractEntityToBeInserted.setApprovedDate(LocalDateTime.now());
		}
		return  map(contractDao.insert(contractEntityToBeInserted));
	}

	/** {@inheritDoc} */
	@Override
	public Report getContractReportById(final String id) {
		final Contract contract = map(getContractEntityById(id, true));;
		if (Contract.Status.APPROVED == contract.getStatus()
				|| ServiceUtil.isUserauthorized(RoleConstants.CONTRACT_APPROVER)) {
			return invoiceHelper.generateInvoice(contract);
		}
		throw new PnsException(PnsError.UNAUTHORIZED);
	}

	/** {@inheritDoc} */
	@Override
	public Contract getContractById(final String id) {
		return map(getContractEntityById(id, false),false);
	}

	private ContractEntity getContractEntityById(final String id, boolean removePo) {
		final ContractEntity contractEntity = contractDao.findById(id).orElseThrow(() -> new PnsException("Contract Not Found!!", PnsError.NOT_FOUND));
		if(removePo) {
			contractEntity.setPoFileContent(null);
		}
		return contractEntity;
	}

	/** {@inheritDoc} */
	@Override
	public Contract modifyContract(final Contract contract) {
	    final ContractEntity existingContract = getContractEntityById(contract.getId(), false);
	    if(Contract.Status.PENDING.name().equals(existingContract.getStatus())) {
	        throw new PnsException("Unable to Update Contract as status is not Approved");
	    }
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
		final ContractEntity contractToBeUpdated = map(
				ImmutableContract.builder().from(contract).amcTax(tax).amcTaxAmount(amcTaxAmount)
						.amcTotalAmount(amcTotalAmount).customer(customerbyid).amcBasicAmount(amcBasicAmount)
						.status(ServiceUtil.isUserauthorized(RoleConstants.CONTRACT_APPROVER) ? Contract.Status.APPROVED
								: Contract.Status.PENDING)
						.equipmentItem(equipmentItem).contractDate(LocalDate.now()).build());
		if (Contract.Status.PENDING.name().equals(contractToBeUpdated.getStatus())) {
            contractToBeUpdated.setOldContract(existingContract);
		}
		contractDao.update(contractToBeUpdated);
		return map(getContractEntityById(contract.getId(), true));
	}

	private double roundUp(final double amount) {
		return Math.round(amount * 100.0) / 100.0;
	}

	/** {@inheritDoc} */
	@Override
	public Contract deleteContractById(final String id) {
		final Contract deletedContract = map(getContractEntityById(id, true));
		contractDao.deleteById(id);
		return deletedContract;
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getContractByProposalNo(final String proposalNo) {
		final Optional<ContractEntity> findByProposalNo = contractDao.findByProposalNo(proposalNo);
		return ImmutableSearchResponse.<Contract>builder()
				.result(findByProposalNo.isPresent() ? List.of(map(findByProposalNo.get())) : Collections.emptyList())
				.pageCount(0).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getContractByAmcStartDateRange(final Range<LocalDate> dateRange, final int page) {
		return ImmutableSearchResponse.<Contract>builder()
				.result(map(contractDao.findContractByAmcDateRange(dateRange, page)))
				.pageCount(contractDao.countDocumnetsByAmcDateRange(dateRange)).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getContractcByCreationDateRange(final Range<LocalDate> dateRange, final int page) {
		return ImmutableSearchResponse.<Contract>builder()
				.result(map(contractDao.findContractByCreationDateRange(dateRange, page)))
				.pageCount(contractDao.countDocumnetsByCreationDateRange(dateRange)).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getAllContract(final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findAll(page)))
				.pageCount(contractDao.countAllDocumnets()).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getContractsByCustomerId(final String customerId, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findByCustomerId(customerId, page)))
				.pageCount(contractDao.countDocumnetsByCustomerId(customerId)).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> getContractsByEquipmentrId(final String equipmentId, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.findByEquipmentId(equipmentId, page)))
				.pageCount(contractDao.countDocumnetsByEquipmentId(equipmentId)).build();
	}

	/** {@inheritDoc} */
	@Override
	public SearchResponse<Contract> searchContractByQuery(final String query, final int page) {
		return ImmutableSearchResponse.<Contract>builder().result(map(contractDao.searchByQuery(query, page)))
				.pageCount(contractDao.countDocumnetsByQuery(query)).build();
	}

	@Override
	public SearchResponse<List<Contract>> getContractsForApproval(int page) {
		final List<List<Contract>> results = contractDao.findAllPendingContracts(page).stream()
				.map(entity -> entity.getOldContract() == null ? List.of(map(entity))
						: List.of(map(entity), map(entity.getOldContract())))
				.collect(Collectors.toList());
		return ImmutableSearchResponse.<List<Contract>>builder().result(results)
				.pageCount(contractDao.contAllPendingContracts()).build();
	}

	@Override
	public Contract approveContract(String contractId) {
		if (contractDao.approve(contractId)) {
			return  map(getContractEntityById(contractId,true));
		}
		log.error("Unable To Approve Contract for contract id : {} ", contractId);
		throw new PnsException("Unable To Approve Contract");
	}


	private List<Contract> map(final List<ContractEntity> list) {
		return list.stream().map(this::map).collect(Collectors.toList());
	}

	private Contract map(final ContractEntity entity) {
		return map(entity, true);
	}

	private Contract map(final ContractEntity entity, boolean removePo) {
		final Builder builder = ImmutableContract.builder().amcBasicAmount(entity.getAmcBasicAmount())
				.amcEndDate(entity.getAmcEndDate()).amcStartDate(entity.getAmcStartDate()).amcTax(entity.getAmcTax())
				.amcTotalAmount(entity.getAmcTotalAmount()).billingCycle(entity.getBillingCycle())
				.customer(entity.getCustomer()).equipmentItem(entity.getEquipmentItem()).note(entity.getNote())
				.id(entity.getId()).contractDate(entity.getContractDate()).proposalNo(entity.getProposalNo())
				.amcTaxAmount(entity.getAmcTaxAmount())
				.status(Contract.Status.valueOf(entity.getStatus() == null ? "PENDING" : entity.getStatus())).poFileName(entity.getPoFileName());
		if (!removePo) {
			builder.poFileContent(entity.getPoFileContent()).poFileContentType(entity.getPoFileContentType());
		}
		return builder.build();

	}

	private ContractEntity map(final Contract contract) {
		// @formatter:off
		final ContractEntity entity = ContractEntity.builder().amcBasicAmount(contract.getAmcBasicAmount())
				.amcEndDate(contract.getAmcEndDate()).amcStartDate(contract.getAmcStartDate())
				.amcTax(contract.getAmcTax()).amcTotalAmount(contract.getAmcTotalAmount())
				.billingCycle(contract.getBillingCycle()).customer(contract.getCustomer())
				.equipmentItem(contract.getEquipmentItem()).note(contract.getNote())
				.contractDate(contract.getContractDate()).proposalNo(contract.getProposalNo())
				.amcTaxAmount(contract.getAmcTaxAmount() != null ? contract.getAmcTaxAmount() : 0)
				.poFileContentType(contract.getPoFileContentType()).poFileContent(contract.getPoFileContent())
				.poFileName(contract.getPoFileName()).status(contract.getStatus().name()).build();
		// @formatter:on
		entity.setId(contract.getId());
		return entity;

	}

}
