package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.dao.LeaveQuotaDao;
import com.pns.contractmanagement.entity.LeaveDetailsEntity;

@Repository
public class LeaveQuotaDaoImpl implements LeaveQuotaDao {

	private final MongoCollection<LeaveDetailsEntity> leaveQuotaCollection;

	@Autowired
	public LeaveQuotaDaoImpl(final MongoCollectionUtil util,
			final @Value("${app.index.name.employee.leaveQuota:leaveQuota}") String leaveQuotaIndex) {
		leaveQuotaCollection = util.getProfileCollection(leaveQuotaIndex, LeaveDetailsEntity.class);
	}

	@Override
	public Optional<LeaveDetailsEntity> findLeaveQuota(final String employeeId, final long year) {
		final LeaveDetailsEntity entity = leaveQuotaCollection
				.find(and(eq("employeeId", employeeId), eq("employeeId", employeeId))).first();
		return Optional.ofNullable(entity);
	}

	@Override
	public LeaveDetailsEntity insert(final LeaveDetailsEntity entity) {
		final InsertOneResult insertOne = leaveQuotaCollection.insertOne(entity);
		entity.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return entity;
	}

}
