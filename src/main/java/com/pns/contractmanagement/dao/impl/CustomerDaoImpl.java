package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static com.pns.contractmanagement.util.DaoUtil.buildCaseInsentiveQuery;
import static com.pns.contractmanagement.util.DaoUtil.countPages;
import static com.pns.contractmanagement.util.DaoUtil.notDeletedFilter;

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
import com.pns.contractmanagement.util.DaoUtil;

/**
 *
 */
@Repository
public class CustomerDaoImpl {

	private static final String REGION = "region";

	@Value("${app.page.size.customer}")
	private int pageSize;

	private final MongoCollection<CustomerEntity> customerCollection;

	/**
	 *
	 */
	@Autowired
	public CustomerDaoImpl(final MongoCollectionUtil util,
			final @Value("${app.index.name.customer:customers}") String customerIndexName) {
		customerCollection = util.getContractCollection(customerIndexName, CustomerEntity.class);
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
				set("name", customer.getName()), set(REGION, customer.getRegion()),
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
		customerCollection.find(and(notDeletedFilter(), new Document(REGION, buildCaseInsentiveQuery(region))))
				.skip((page - 1) * pageSize).limit(pageSize).iterator().forEachRemaining(customers::add);
		return customers;
	}

	public boolean deleteById(final String id) {
		final UpdateResult ur = customerCollection.updateOne(eq("_id", new ObjectId(id)), DaoUtil.deleteBsonDoc());
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public List<CustomerEntity> findAll(final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(notDeletedFilter()).skip((page - 1) * pageSize).limit(pageSize).iterator()
				.forEachRemaining(customers::add);
		return customers;
	}

	public long countAllDocumnets() {
		final long countDocuments = customerCollection.countDocuments(notDeletedFilter());
		return countPages(countDocuments, pageSize);
	}

	public List<CustomerEntity> searchByQuery(final String query, final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(and(text(query), notDeletedFilter())).skip((page - 1) * pageSize).limit(pageSize)
				.iterator().forEachRemaining(customers::add);
		return customers;
	}

	public long countDocumnetsByQuery(final String query) {
		final long countDocuments = customerCollection.countDocuments(and(text(query), notDeletedFilter()));
		return countPages(countDocuments, pageSize);
	}

	public Collection<CustomerEntity> findByName(final String name, final int page) {
		final List<CustomerEntity> customers = new ArrayList<>();
		customerCollection.find(and(notDeletedFilter(), new Document("name", buildCaseInsentiveQuery(name))))
				.skip((page - 1) * pageSize).limit(pageSize).iterator().forEachRemaining(customers::add);
		return customers;
	}

	public long countDocumnetsByName(final String name) {
		final long countDocuments = customerCollection
				.countDocuments(and(notDeletedFilter(), new Document("name", buildCaseInsentiveQuery(name))));
		return countPages(countDocuments, pageSize);
	}

	public long countDocumnetsByRegion(final String region) {
		final long countDocuments = customerCollection
				.countDocuments(and(notDeletedFilter(), new Document(REGION, buildCaseInsentiveQuery(region))));
		return countPages(countDocuments, pageSize);
	}
}
