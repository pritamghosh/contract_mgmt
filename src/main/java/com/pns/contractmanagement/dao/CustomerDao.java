package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static com.pns.contractmanagement.dao.DaoUtil.NOT_DELETED_FILTER;
import static com.pns.contractmanagement.dao.DaoUtil.buildCaseInsentiveQuery;
import static com.pns.contractmanagement.dao.DaoUtil.countPages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.CustomerEntity;

/**
 *
 */
@Repository
public class CustomerDao {

	@Value("${app.page.size.customer}")
	private int pageSize;

	private final MongoCollection<CustomerEntity> customerCollection;

	/**
	 *
	 */
	@Autowired
	public CustomerDao(final MongoCollectionUtil util,
			final @Value("${app.index.name.customer:customers}") String customerIndexName) {
		customerCollection = util.getCollection(customerIndexName, CustomerEntity.class);
	}

	public CustomerEntity insert(final CustomerEntity customer) {
		DaoUtil.setCreationDetails(customer);
		final InsertOneResult insertOne = customerCollection.insertOne(customer);
		customer.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return customer;
	}

	public boolean update(final CustomerEntity customer) {
		DaoUtil.setModificationDetails(customer);
		final Bson update = combine(
				// @formatter:off
				set("name", customer.getName()), set("region", customer.getRegion()),
				set("address", customer.getAddress()), set("gstinNo", customer.getGstinNo()),
				set("pan", customer.getPan()), set("lastModifiedBy", customer.getLastModifiedBy()),
				set("lastModifiedDate", customer.getLastModifiedDate())
		// @formatter:on
		);
		final UpdateResult ur = customerCollection.updateOne(and(eq("_id", customer.getOid())), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public Optional<CustomerEntity> findById(final String id) {
		return Optional.ofNullable(customerCollection.find(new Document("_id", new ObjectId(id))).first());
	}

	public List<CustomerEntity> findByRegion(final String region, final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(and(NOT_DELETED_FILTER, new Document("region", buildCaseInsentiveQuery(region))))
				.skip((page - 1) * pageSize).limit(pageSize).iterator().forEachRemaining(customers::add);
		return customers;
	}

	public boolean deleteById(final String id) {
		final UpdateResult ur = customerCollection.updateOne(eq("_id", new ObjectId(id)), DaoUtil.deleteBsonDoc());
		// final DeleteResult deleteOne = customerCollection.deleteOne(and(eq("_id", new
		// ObjectId(id))));
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public List<CustomerEntity> findAll(final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(NOT_DELETED_FILTER).skip((page - 1) * pageSize).limit(pageSize).iterator()
				.forEachRemaining(customers::add);
		return customers;
	}

	public long countAllDocumnets() {
		final long countDocuments = customerCollection.countDocuments(NOT_DELETED_FILTER);
		return countPages(countDocuments, pageSize);
	}

	public List<CustomerEntity> searchByQuery(final String query, final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(and(text(query), NOT_DELETED_FILTER)).skip((page - 1) * pageSize).limit(pageSize)
				.iterator().forEachRemaining(customers::add);
		return customers;
	}

	public long countDocumnetsByQuery(final String query) {
		final long countDocuments = customerCollection.countDocuments(and(text(query), NOT_DELETED_FILTER));
		return countPages(countDocuments, pageSize);
	}

	public Collection<CustomerEntity> findByName(final String name, final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(and(NOT_DELETED_FILTER, new Document("name", buildCaseInsentiveQuery(name))))
				.skip((page - 1) * pageSize).limit(pageSize).iterator().forEachRemaining(customers::add);
		return customers;
	}

	public long countDocumnetsByName(final String name) {
		final long countDocuments = customerCollection
				.countDocuments(and(NOT_DELETED_FILTER, new Document("name", buildCaseInsentiveQuery(name))));
		return countPages(countDocuments, pageSize);
	}

	public long countDocumnetsByRegion(final String region) {
		final long countDocuments = customerCollection
				.countDocuments(and(NOT_DELETED_FILTER, new Document("region", buildCaseInsentiveQuery(region))));
		return countPages(countDocuments, pageSize);
	}
}
