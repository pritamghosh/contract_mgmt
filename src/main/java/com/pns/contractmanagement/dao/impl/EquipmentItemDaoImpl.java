package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.EquipmentItemEntity;
import com.pns.contractmanagement.util.DaoUtil;

/**
 *
 */
@Repository
public class EquipmentItemDaoImpl {

    private final MongoCollection<EquipmentItemEntity> equipmentItemCollection;

    private final MongoCollection<Document> equipmentItemDocCollection;

    @Autowired
    public EquipmentItemDaoImpl(final MongoCollectionUtil util,
        final @Value("${app.index.name.equipmentItem:equipmentItems}") String equipmentIndexName) {
        equipmentItemCollection = util.getContractCollection(equipmentIndexName, EquipmentItemEntity.class);
        equipmentItemDocCollection = util.getContractDatabase().getCollection(equipmentIndexName);
    }

    public EquipmentItemEntity insert(final EquipmentItemEntity equipmentItem) {
		DaoUtil.setCreationDetails(equipmentItem);
        final InsertOneResult insertOne = equipmentItemCollection.insertOne(equipmentItem);
        equipmentItem.setOid(insertOne.getInsertedId().asObjectId().getValue());
        return equipmentItem;
    }

    public boolean update(final EquipmentItemEntity equipmentItem) {
    	DaoUtil.setModificationDetails(equipmentItem);
        Bson update = combine(set("serialNumber", equipmentItem.getSerialNumber()),
            set("equipmentDetailObjectId", equipmentItem.getEquipmentObjectId()),
			set("lastModifiedBy", equipmentItem.getLastModifiedBy()),
			set("lastModifiedDate", equipmentItem.getLastModifiedDate()));
        UpdateResult ur = equipmentItemCollection.updateOne(and(eq("_id", equipmentItem.getOid())), update);
        return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
    }

    public Optional<EquipmentItemEntity> findById(final String id) {
        return Optional.ofNullable(equipmentItemCollection.find(new Document("_id", new ObjectId(id))).first());
    }

    public boolean deleteById(final String id) {

        DeleteResult deleteOne = equipmentItemCollection.deleteOne(and(eq("_id", new ObjectId(id))));
        return deleteOne.getDeletedCount() > 0;
    }

    public List<EquipmentItemEntity> findAll() {
        final List<EquipmentItemEntity> equipments = new ArrayList<>();
        equipmentItemCollection.find().iterator().forEachRemaining(equipments::add);
        return equipments;
    }

    public List<EquipmentItemEntity> searchByQuery(final String query) {
        final List<EquipmentItemEntity> equipmentItems = new ArrayList<>();
        equipmentItemCollection.find(text(query)).iterator().forEachRemaining(equipmentItems::add);
        return equipmentItems;
    }

    public long getCountById(String id) {
        return equipmentItemCollection.countDocuments(eq("equipment_id", new ObjectId(id)));
    }

    public Map<String, Long> getCount() {
        Map<String, Long> countMap = new HashMap<>();
        AggregateIterable<Document> aggregate = equipmentItemDocCollection
            .aggregate(Arrays.asList(group("$equipment_id", sum("count", 1L))));
        aggregate.iterator()
            .forEachRemaining(d -> countMap.put(((ObjectId) d.get("_id")).toHexString(), (Long) d.get("count")));
        return countMap;
    }
}
