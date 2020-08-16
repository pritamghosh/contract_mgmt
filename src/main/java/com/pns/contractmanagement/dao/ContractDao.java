package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.ContractEntity;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.EquipmentItem;

/**
 *
 */
@Repository
public class ContractDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDao.class);
	@Value("${app.page.size.contract}")
	private int pageSize;
	private final MongoCollection<ContractEntity> contractCollection;

	private final MongoCollection<Document> contractDocumentCollection;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 *
	 */
	@Autowired
	public ContractDao(final MongoCollectionUtil util,
			final @Value("${app.index.name.contract:contracts}") String contractIndexName) {
		contractCollection = util.getCollection(contractIndexName, ContractEntity.class);
		contractDocumentCollection = util.getCollection(contractIndexName);
	}

	public ContractEntity insert(final ContractEntity contract) {
		contract.setCustomerOid(new ObjectId(contract.getCustomer().getId()));
		contract.setEquipmnetOid(new ObjectId(contract.getEquipmentItem().getEquipment().getId()));
		final InsertOneResult insertOne = contractCollection.insertOne(contract);
		contract.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return contract;
	}

	public boolean update(final ContractEntity contract) {

		final Bson update = combine(
				// @formatter:off
				set("customer", contract.getCustomer()), set("equipmentItem", contract.getEquipmentItem()),
				set("amcBasicAmount", contract.getAmcBasicAmount()), set("amcEndDate", contract.getAmcEndDate()),
				set("amcStartDate", contract.getAmcStartDate()), set("amcTax", contract.getAmcTax()),
				set("amcTaxAmount", contract.getAmcTaxAmount()), set("proposalNo", contract.getProposalNo()),
				set("amcTotalAmount", contract.getAmcTotalAmount()), set("billingCycle", contract.getBillingCycle()),
				set("note", contract.getNote()), set("equipmnetOid", new ObjectId(contract.getCustomer().getId())),
				set("customerOid", new ObjectId(contract.getEquipmentItem().getEquipment().getId()))
		// @formatter:on
		);
		final UpdateResult ur = contractCollection.updateOne(eq("_id", contract.getOid()), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public Optional<ContractEntity> findById(final String id) {
		return Optional.ofNullable(map(contractDocumentCollection.find(new Document("_id", new ObjectId(id))).first()));
	}

	public List<ContractEntity> findByCustomerId(final String customerId, final int page) {
		return map(contractDocumentCollection.find(new Document("customerOid", new ObjectId(customerId)))
				.skip((page - 1) * pageSize).limit(pageSize).limit(pageSize));
	}

	public long countDocumnetsByCustomerId(final String customerId) {
		return DaoUtil.countPages(
				contractDocumentCollection.countDocuments(new Document("customerOid", new ObjectId(customerId))),
				pageSize);
	}

	public List<ContractEntity> findByEquipmentId(final String equipmentId, final int page) {
		return map(contractDocumentCollection.find(new Document("equipmnetOid", new ObjectId(equipmentId)))
				.skip((page - 1) * pageSize).limit(pageSize));
	}

	public long countDocumnetsByEquipmentId(final String equipmentId) {
		return DaoUtil.countPages(
				contractDocumentCollection.countDocuments(new Document("equipmnetOid", new ObjectId(equipmentId))),
				pageSize);
	}

	public List<ContractEntity> findByEuipmentSerialNo(final String equipmentSerialNo) {
		return map(contractDocumentCollection.find(new Document("equipmentItem.serialNumber", equipmentSerialNo)));
	}

	public boolean deleteById(final String id) {
		final DeleteResult deleteOne = contractCollection.deleteOne(and(eq("_id", new ObjectId(id))));
		return deleteOne.getDeletedCount() > 0;
	}

	public List<ContractEntity> findAll(final int page) {
		return map(contractDocumentCollection.find().skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> findContractByAmcDateRange(final Range<LocalDate> dateRange, final int page) {
		return map(contractDocumentCollection
				.find(and(new Document("amcStartDate", new Document("$gte", dateRange.lowerEndpoint())),
						new Document("amcEndDate", new Document("$lte", dateRange.upperEndpoint()))))
				.skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> findContractByCreationDateRange(final Range<LocalDate> dateRange, final int page) {
		return map(contractDocumentCollection
				.find(new Document("contractDate",
						new Document("$gte", dateRange.lowerEndpoint()).append("$lte", dateRange.upperEndpoint())))
				.skip((page - 1) * pageSize).limit(pageSize));
	}

	public List<ContractEntity> searchByQuery(final String query, final int page) {
		return map(contractDocumentCollection.find(text(query)).skip((page - 1) * pageSize).limit(pageSize));
	}

	public long countAllDocumnets() {
		return DaoUtil.countPages(contractDocumentCollection.countDocuments(), pageSize);
	}

	public long countDocumnetsByQuery(final String query) {
		return DaoUtil.countPages(contractDocumentCollection.countDocuments(text(query)), pageSize);
	}

	public long countDocumnetsByAmcDateRange(final Range<LocalDate> dateRange) {
		return DaoUtil.countPages(
				contractDocumentCollection.countDocuments(
						and(new Document("amcStartDate", new Document("$gte", dateRange.lowerEndpoint())),
								new Document("amcEndDate", new Document("$lte", dateRange.upperEndpoint())))),
				pageSize);
	}

	public long countDocumnetsByCreationDateRange(final Range<LocalDate> dateRange) {
		return DaoUtil.countPages(
				contractDocumentCollection.countDocuments(new Document("contractDate",
						new Document("$gte", dateRange.lowerEndpoint()).append("$lte", dateRange.upperEndpoint()))),
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
			final ContractEntity entity = ContractEntity.builder().amcBasicAmount(document.getDouble("amcBasicAmount"))
					.amcEndDate(Instant.ofEpochMilli(document.getDate("amcEndDate").getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.amcStartDate(Instant.ofEpochMilli(document.getDate("amcStartDate").getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.amcTax(document.getDouble("amcTax")).amcTotalAmount(document.getDouble("amcTotalAmount"))
					.billingCycle(document.getString("billingCycle")).note(document.getString("note"))
					.customer(customer).equipmentItem(equipment)
					.contractDate(Instant.ofEpochMilli(document.getDate("contractDate").getTime())
							.atZone(ZoneId.systemDefault()).toLocalDate())
					.proposalNo(document.getString("proposalNo")).amcTaxAmount(document.getDouble("amcTaxAmount"))
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
		return StreamSupport.stream(mongoIterable.spliterator(), true).map(d -> map(d)).collect(Collectors.toList());
	}

}
