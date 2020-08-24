package com.pns.contractmanagement.dao;

import java.time.LocalDate;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.entity.SequenceEntity;

/**
 *
 */
@Repository
public class SystemDao {

	private final MongoDatabase databese;

	@Value("${app.switch.index.update.customer:false}")
	private boolean isCutomerIndexUpdateAllowed;
	@Value("${app.switch.index.update.equipment:false}")
	private boolean isEquipmentIndexUpdateAllowed;
	@Value("${app.switch.index.update.contract:false}")
	private boolean isContractIndexUpdateAllowed;
	@Value("${app.index.name.customer:customers}")
	private String customerIndexName;
	@Value("${app.index.name.contract:contracts}")
	private String contractIndexName;
	@Value("${app.index.name.equipment:equipments}")
	private String equipmentIndexName;
	private final MongoCollection<SequenceEntity> sequenceDocumentCollection;

	@Autowired
	public SystemDao(final MongoCollectionUtil util,
			final @Value("${app.index.name.sequence:sequences}") String sequenceIndexName) {
		databese = util.getDb();
		sequenceDocumentCollection = util.getCollection(sequenceIndexName, SequenceEntity.class);
	}

	public void initIndexes() {
		if (isCutomerIndexUpdateAllowed) {
			final MongoCollection<Document> collection = databese.getCollection(customerIndexName);
			dropExistingTextIndex(collection);
			collection.createIndex(Indexes.compoundIndex(Indexes.text("name"), Indexes.text("region")));
		}

		if (isContractIndexUpdateAllowed) {
			final MongoCollection<Document> collection = databese.getCollection(contractIndexName);
			dropExistingTextIndex(collection);
			collection.createIndex(Indexes.compoundIndex(Indexes.text("customer.name"), Indexes.text("customer.region"),
					Indexes.text("equipmentItem.serialNumber"), Indexes.text("equipmentItem.equipment.model"),
					Indexes.text("billingCycle"),Indexes.text("proposalNo")));
		}

		if (isEquipmentIndexUpdateAllowed) {
			final MongoCollection<Document> collection = databese.getCollection(equipmentIndexName);
			dropExistingTextIndex(collection);
			collection.createIndex(Indexes.compoundIndex(Indexes.text("model")));
		}
	}

	/**
	 * @param collection
	 */
	private void dropExistingTextIndex(final MongoCollection<Document> collection) {
		StreamSupport.stream(collection.listIndexes().spliterator(), false)
				.filter(d -> d.get("key") != null && "text".equals(((Document) d.get("key")).get("_fts")))
				.forEach(d -> collection.dropIndex(d.getString("name")));
	}

	public boolean initSequece(final int seq) {
		sequenceDocumentCollection.deleteOne(new Document("squenceType", "proposal-no"));
		final InsertOneResult insertOne = sequenceDocumentCollection.insertOne(
				SequenceEntity.builder().date(LocalDate.now()).sequence(seq).squenceType("proposal-no").build());
		return insertOne.getInsertedId() != null;
	}
}
