package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;
import static com.pns.contractmanagement.dao.DaoUtil.countPages;
import static com.pns.contractmanagement.dao.DaoUtil.notDeletedFilter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.EquipmentItem;

/**
 *
 */
@Repository
public class ContractDao {
	private static final String CONTRACT_DATE = "contractDate";
	private static final String PROPOSAL_NO_SEQUENCEE = "proposal-no";
	private static final String BILLING_CYCLE = "billingCycle";
	private static final String EQUIPMNET_OID = "equipmnetOid";
	private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String LAST_MODIFIED_BY = "lastModifiedBy";
	private static final String CUSTOMER_OID = "customerOid";
	private static final String AMC_TOTAL_AMOUNT = "amcTotalAmount";
	private static final String AMC_TAX_AMOUNT = "amcTaxAmount";
	private static final String AMC_START_DATE = "amcStartDate";
	private static final String AMC_BASIC_AMOUNT = "amcBasicAmount";
	private static final String AMC_END_DATE = "amcEndDate";
	private static final String AMC_TAX = "amcTax";
	private static final String PROPOSAL_NO = "proposalNo";
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDao.class);
	@Value("${app.page.size.contract}")
	private int pageSize;
	private final MongoCollection<ContractEntity> contractCollection;

	private final MongoCollection<Document> contractDocumentCollection;

	private final MongoCollection<SequenceEntity> sequenceDocumentCollection;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 *
	 */
	@Autowired
	public ContractDao(final MongoCollectionUtil util,
			final @Value("${app.index.name.contract:contracts}") String contractIndexName,
			final @Value("${app.index.name.sequence:sequences}") String sequenceIndexName) {
		contractCollection = util.getCollection(contractIndexName, ContractEntity.class);
		contractDocumentCollection = util.getCollection(contractIndexName);
		sequenceDocumentCollection = util.getCollection(sequenceIndexName, SequenceEntity.class);
	}

	public ContractEntity insert(final ContractEntity contract) {
		DaoUtil.setCreationDetails(contract);
		contract.setCustomerOid(new ObjectId(contract.getCustomer().getId()));
		contract.setEquipmnetOid(new ObjectId(contract.getEquipmentItem().getEquipment().getId()));
		final InsertOneResult insertOne = contractCollection.insertOne(contract);
		contract.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return contract;
	}

	public SequenceEntity findAndUpdateSequece() {
		SequenceEntity sequence = sequenceDocumentCollection.findOneAndUpdate(
				and(new Document("squenceType", PROPOSAL_NO_SEQUENCEE), new Document("date", LocalDate.now())),
				inc("sequence", 1));
		if (sequence == null) {
			sequence = sequenceDocumentCollection.findOneAndUpdate((new Document("squenceType", PROPOSAL_NO_SEQUENCEE)),
					combine(set("date", LocalDate.now()), set("sequence", 1)));
		} else {
			sequence.setSequence(sequence.getSequence() + 1);
			return sequence;
		}
		if (sequence == null) {
			sequence = SequenceEntity.builder().date(LocalDate.now()).sequence(1).squenceType(PROPOSAL_NO_SEQUENCEE)
					.build();
			sequenceDocumentCollection.insertOne(sequence);
		} else {
			sequence.setSequence(1);
			sequence.setDate(LocalDate.now());
		}
		return sequence;

	}

	public boolean update(final ContractEntity contract) {
		DaoUtil.setModificationDetails(contract);
		final Bson update = combine(
				// @formatter:off
				set("customer", contract.getCustomer()), set("equipmentItem", contract.getEquipmentItem()),
				set(AMC_BASIC_AMOUNT, contract.getAmcBasicAmount()), set(AMC_END_DATE, contract.getAmcEndDate()),
				set(AMC_START_DATE, contract.getAmcStartDate()), set(AMC_TAX, contract.getAmcTax()),
				set(AMC_TAX_AMOUNT, contract.getAmcTaxAmount()), set(PROPOSAL_NO, contract.getProposalNo()),
				set(AMC_TOTAL_AMOUNT, contract.getAmcTotalAmount()), set(BILLING_CYCLE, contract.getBillingCycle()),
				set("note", contract.getNote()), set(EQUIPMNET_OID, new ObjectId(contract.getCustomer().getId())),
				set(CUSTOMER_OID, new ObjectId(contract.getEquipmentItem().getEquipment().getId())),
				set(LAST_MODIFIED_BY, contract.getLastModifiedBy()),
				set(LAST_MODIFIED_DATE, contract.getLastModifiedDate())

		// @formatter:on
		);
		final UpdateResult ur = contractCollection.updateOne(eq("_id", contract.getOid()), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public Optional<ContractEntity> findById(final String id) {
		return Optional.ofNullable(map(contractDocumentCollection.find(new Document("_id", new ObjectId(id))).first()));
	}

	public List<ContractEntity> findByCustomerId(final String customerId, final int page) {
		return map(contractDocumentCollection.find(new Document(CUSTOMER_OID, new ObjectId(customerId)))
				.skip((page - 1) * pageSize).limit(pageSize).limit(pageSize));
	}

	public long countDocumnetsByCustomerId(final String customerId) {
		return countPages(
				contractDocumentCollection.countDocuments(new Document(CUSTOMER_OID, new ObjectId(customerId))),
				pageSize);
	}

	public List<ContractEntity> findByEquipmentId(final String equipmentId, final int page) {
		return map(contractDocumentCollection.find(new Document(EQUIPMNET_OID, new ObjectId(equipmentId)))
				.skip((page - 1) * pageSize).limit(pageSize));
	}

	public long countDocumnetsByEquipmentId(final String equipmentId) {
		return countPages(
				contractDocumentCollection.countDocuments(new Document(EQUIPMNET_OID, new ObjectId(equipmentId))),
				pageSize);
	}

	public List<ContractEntity> findByEuipmentSerialNo(final String equipmentSerialNo) {
		return map(contractDocumentCollection.find(new Document("equipmentItem.serialNumber", equipmentSerialNo)));
	}

	public boolean deleteById(final String id) {
		final UpdateResult ur = contractCollection.updateOne(eq("_id", new ObjectId(id)), DaoUtil.deleteBsonDoc());
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public List<ContractEntity> findAll(final int page) {
		return map(contractDocumentCollection.find(notDeletedFilter()).skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> findContractByAmcDateRange(final Range<LocalDate> dateRange, final int page) {
		return map(contractDocumentCollection
				.find(and(notDeletedFilter(),
						new Document(AMC_START_DATE, new Document("$gte", dateRange.lowerEndpoint())),
						new Document(AMC_END_DATE, new Document("$lte", dateRange.upperEndpoint()))))
				.skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> findContractByCreationDateRange(final Range<LocalDate> dateRange, final int page) {
		return map(
				contractDocumentCollection
						.find(and(notDeletedFilter(),
								new Document(CONTRACT_DATE,
										new Document("$gte", dateRange.lowerEndpoint()).append("$lte",
												dateRange.upperEndpoint()))))
						.skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> searchByQuery(final String query, final int page) {
		return map(contractDocumentCollection.find(and(notDeletedFilter(), text(query))).skip((page - 1) * pageSize)
				.limit(pageSize));
	}

	public long countAllDocumnets() {
		return countPages(contractDocumentCollection.countDocuments(notDeletedFilter()), pageSize);
	}

	public long countDocumnetsByQuery(final String query) {
		return countPages(contractDocumentCollection.countDocuments(and(notDeletedFilter(), text(query))), pageSize);
	}

	public long countDocumnetsByAmcDateRange(final Range<LocalDate> dateRange) {
		return countPages(contractDocumentCollection.countDocuments(
				and(notDeletedFilter(), new Document(AMC_START_DATE, new Document("$gte", dateRange.lowerEndpoint())),
						new Document(AMC_END_DATE, new Document("$lte", dateRange.upperEndpoint())))),
				pageSize);
	}

	public long countDocumnetsByCreationDateRange(final Range<LocalDate> dateRange) {
		return countPages(
				contractDocumentCollection.countDocuments(and(notDeletedFilter(), new Document(CONTRACT_DATE,
						new Document("$gte", dateRange.lowerEndpoint()).append("$lte", dateRange.upperEndpoint())))),
				pageSize);
	}

	private ContractEntity map(final Document document) {
		if (document == null) {
			return null;
		}
		try {
			final Customer customer = objectMapper.readValue(((Document) document.get("customer")).toJson(),
					Customer.class);
			final EquipmentItem equipment = objectMapper.readValue(((Document) document.get("equipmentItem")).toJson(),
					EquipmentItem.class);

			// @formatter:off
			final ContractEntity entity = ContractEntity.builder()
					.amcBasicAmount(document.getDouble(ContractDao.AMC_BASIC_AMOUNT))
					.amcEndDate(Instant.ofEpochMilli(document.getDate(AMC_END_DATE).getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.amcStartDate(Instant.ofEpochMilli(document.getDate(AMC_START_DATE).getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.amcTax(document.getDouble(AMC_TAX)).amcTotalAmount(document.getDouble(AMC_TOTAL_AMOUNT))
					.billingCycle(document.getString(BILLING_CYCLE)).note(document.getString("note")).customer(customer)
					.equipmentItem(equipment)
					.contractDate(Instant.ofEpochMilli(document.getDate(CONTRACT_DATE).getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.proposalNo(document.getString(PROPOSAL_NO)).amcTaxAmount(document.getDouble(AMC_TAX_AMOUNT))
					.build();
			// @formatter:on
			entity.setOid(document.getObjectId("_id"));
			return entity;
		} catch (final JsonProcessingException ex) {
			LOGGER.error("JSON parsing exception", ex);
			return null;
		}
	}

	/**
	 * @param find
	 * @return
	 */
	private List<ContractEntity> map(final FindIterable<Document> mongoIterable) {
		return StreamSupport.stream(mongoIterable.spliterator(), true).map(this::map).collect(Collectors.toList());
	}

}
