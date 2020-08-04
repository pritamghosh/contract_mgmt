package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.CustomerEntity;

/**
 *
 */
@Repository
public class CustomerDao {
    private final MongoCollection<CustomerEntity> customerCollection;
    

    /**
     * 
     */
    @Autowired
    public CustomerDao(final MongoCollectionUtil util ,final @Value("${app.index.name.customer:customers}") String customerIndexName) {
        customerCollection = util.getCollection(customerIndexName, CustomerEntity.class);
    }

    public CustomerEntity insert(final CustomerEntity customer) {
        final InsertOneResult insertOne = customerCollection.insertOne(customer);
        customer.setOid(insertOne.getInsertedId().asObjectId().getValue());
        return customer;
    }
    
    public boolean update(final CustomerEntity customer) {
        
        Bson update = combine(set("name", customer.getName()), set("region",  customer.getRegion()));
        UpdateResult ur = customerCollection.updateOne(and(eq("_id", customer.getOid())),update);
        return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
    }

    public Optional<CustomerEntity> findById(final String id) {
        return Optional.ofNullable(customerCollection.find(new Document("_id", new ObjectId(id))).first());
    }
    
    public List<CustomerEntity> findByRegion(final String region) {
        Document regQuery = new Document();
        regQuery.append("$regex", "^(?)" + region);
        regQuery.append("$options", "i");
        final List<CustomerEntity> customers = new ArrayList<>();
        customerCollection.find(new Document("region", region)).iterator().forEachRemaining(customers::add);
        return customers;
    }
    
    public boolean deleteById(final String id) {

        DeleteResult deleteOne = customerCollection.deleteOne(and(eq("_id", new ObjectId(id))));
        return deleteOne.getDeletedCount()>0;
    }

    public List<CustomerEntity> findAll() {
        final List<CustomerEntity> customers = new ArrayList<>();
        customerCollection.find().iterator().forEachRemaining(customers::add);
        return customers;
    }
    
    public List<CustomerEntity> searchByQuery(final String query) {
        final List<CustomerEntity> customers = new ArrayList<>();
        customerCollection.find(text(query)).iterator().forEachRemaining(customers::add);
        return customers;
    }
}
