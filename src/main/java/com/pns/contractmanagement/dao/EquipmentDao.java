package com.pns.contractmanagement.dao;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static com.pns.contractmanagement.dao.DaoUtil.buildCaseInsentiveQuery;
import static com.pns.contractmanagement.dao.DaoUtil.countPages;
import static com.pns.contractmanagement.dao.DaoUtil.notDeletedFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.entity.EquipmentEntity;

/**
 *
 */
@Repository
public class EquipmentDao {
	private static final String MODEL = "model";

	@Value("${app.page.size.equipment}")
	private int pageSize;

	private final MongoCollection<EquipmentEntity> equipmentCollection;

	@Autowired
	public EquipmentDao(final MongoCollectionUtil util,
			final @Value("${app.index.name.equipment:equipments}") String equipmentEntityIndexName) {
		equipmentCollection = util.getCollection(equipmentEntityIndexName, EquipmentEntity.class);
	}

	public EquipmentEntity insert(final EquipmentEntity equipmentEntity) {
		DaoUtil.setCreationDetails(equipmentEntity);
		final InsertOneResult insertOne = equipmentCollection.insertOne(equipmentEntity);
		equipmentEntity.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return equipmentEntity;
	}

	public boolean update(final EquipmentEntity equipmentEntity) {
		DaoUtil.setModificationDetails(equipmentEntity);
		final Bson update = combine(set(MODEL, equipmentEntity.getModel()),
				set("description", equipmentEntity.getDescription()),
				set("lastModifiedBy", equipmentEntity.getLastModifiedBy()),
				set("lastModifiedDate", equipmentEntity.getLastModifiedDate()));
		final UpdateResult ur = equipmentCollection.updateOne(and(eq("_id", equipmentEntity.getOid())), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public Optional<EquipmentEntity> findById(final String id) {
		return Optional.ofNullable(equipmentCollection.find(new Document("_id", new ObjectId(id))).first());
	}

	public Map<String, EquipmentEntity> findByIds(final List<String> ids) {
		final Map<String, EquipmentEntity> equipmentEntity = new HashMap<>();
		equipmentCollection.find(Filters.all("_id", ids.stream().map(ObjectId::new).collect(Collectors.toList())))
				.iterator().forEachRemaining(e -> equipmentEntity.put(e.getId(), e));
		return equipmentEntity;
	}

	public boolean deleteById(final String id) {
		final UpdateResult ur = equipmentCollection.updateOne(eq("_id", new ObjectId(id)), DaoUtil.deleteBsonDoc());
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

	public List<EquipmentEntity> findByModel(final String model, final int page) {
		final List<EquipmentEntity> equipmentEntities = new ArrayList<>();
		equipmentCollection.find(and(notDeletedFilter(), new Document(MODEL, buildCaseInsentiveQuery(model))))
				.skip((page - 1) * pageSize).limit(pageSize).iterator().forEachRemaining(equipmentEntities::add);
		return equipmentEntities;
	}

	public long countDocumnetsByModel(final String model) {
		return countPages(equipmentCollection.countDocuments(
				and(notDeletedFilter(), new Document(MODEL, buildCaseInsentiveQuery(model)))), pageSize);
	}

	public List<EquipmentEntity> findAll(final int page) {
		final List<EquipmentEntity> equipmentEntity = new ArrayList<>();
		equipmentCollection.find(notDeletedFilter()).skip((page - 1) * pageSize).limit(pageSize).iterator()
				.forEachRemaining(equipmentEntity::add);
		return equipmentEntity;
	}

	public long countAllDocumnets() {
		return countPages(equipmentCollection.countDocuments(notDeletedFilter()), pageSize);
	}

	public List<EquipmentEntity> searchByQuery(final String query, final int page) {
		final List<EquipmentEntity> equipmentEntities = new ArrayList<>();
		equipmentCollection.find(and(notDeletedFilter(), text(query))).skip((page - 1) * pageSize).limit(pageSize)
				.iterator().forEachRemaining(equipmentEntities::add);
		return equipmentEntities;
	}

	public long countDocumnetsByQuery(final String query) {
		return countPages(equipmentCollection.countDocuments(and(notDeletedFilter(), text(query))), pageSize);
	}

}
