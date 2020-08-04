package com.pns.contractmanagement.dao;

import java.util.stream.StreamSupport;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

/**
 *
 */
@Repository
public class SystemDao {

    private MongoDatabase databese;

    @Value("${app.switch.index.update.customer:false}")
    private boolean isCutomerIndexUpdateAllowed;
    @Value("${app.switch.index.update.contract:false}")
    private boolean isContractIndexUpdateAllowed;
    @Value("${app.index.name.customer:customers}")
    private String customerIndexName;
    @Value("${app.index.name.contract:contracts}")
    private String contractIndexName;

    @Autowired
    public SystemDao(final MongoCollectionUtil util) {
        databese = util.getDb();
    }

    public void initIndexes() {
        if (isCutomerIndexUpdateAllowed) {
            MongoCollection<Document> collection = databese.getCollection(customerIndexName);
            dropExistingTextIndex(collection);
            collection.createIndex(Indexes.compoundIndex(Indexes.text("name"), Indexes.text("region")));
        }

        if (isContractIndexUpdateAllowed) {
            MongoCollection<Document> collection = databese.getCollection(contractIndexName);
            dropExistingTextIndex(collection);
            collection.createIndex(Indexes.compoundIndex(Indexes.text("customer.name"), Indexes.text("customer.region"),
                Indexes.text("equipmentItem.serialNumber"), Indexes.text("billingCycle")));
        }
    }

    /**
     * @param collection
     */
    private void dropExistingTextIndex(MongoCollection<Document> collection) {
        StreamSupport.stream(collection.listIndexes().spliterator(), false)
            .filter(d -> d.get("key") != null && "text".equals(((Document) d.get("key")).get("_fts")))
            .forEach(d -> collection.dropIndex(d.getString("name")));
    }
}
