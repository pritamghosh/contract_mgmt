package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.dao.LeaveQuotaDao;
import com.pns.contractmanagement.entity.LeaveDetailsEntity;
import com.pns.contractmanagement.entity.LeaveDetailsEntity.LeaveQuotaEntity;
import com.pns.contractmanagement.entity.LeaveRequestEntity;
import com.pns.contractmanagement.exceptions.PnsException;

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
		final LeaveDetailsEntity entity = leaveQuotaCollection.find(and(eq("employeeId", employeeId), eq("year", year)))
				.first();
		return Optional.ofNullable(entity);
	}

	@Override
	public LeaveDetailsEntity insert(final LeaveDetailsEntity entity) {
		final InsertOneResult insertOne = leaveQuotaCollection.insertOne(entity);
		entity.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return entity;
	}

	@Override
	public LeaveDetailsEntity updateLeaveQuota(LeaveRequestEntity entity, int year) {
		final LeaveDetailsEntity findOneAndUpdate = leaveQuotaCollection.findOneAndUpdate(
				and(eq("employeeId", entity.getEmployeeId()), eq("year", year),
						eq("leaveQuota.type", entity.getType().name())),
				combine(inc("leaveQuota.$.usedLeaves", entity.getNoOfDays()),
						inc("leaveQuota.$.approvalPendingLeaves", entity.getNoOfDays()),
						inc("leaveQuota.$.reameningLeaves", -1 * entity.getNoOfDays())));
		final LeaveQuotaEntity leaveQuotaEntity = findOneAndUpdate.getLeaveQuota().stream()
				.filter(e -> e.getType() == entity.getType()).findFirst().get();
		leaveQuotaEntity.setReameningLeaves(leaveQuotaEntity.getReameningLeaves() - entity.getNoOfDays());
		leaveQuotaEntity.setApprovalPendingLeaves(leaveQuotaEntity.getApprovalPendingLeaves() + entity.getNoOfDays());
		leaveQuotaEntity.setUsedLeaves(leaveQuotaEntity.getUsedLeaves() + entity.getNoOfDays());
		if (leaveQuotaEntity.getReameningLeaves() < entity.getNoOfDays()) {
			leaveQuotaCollection.findOneAndUpdate(
					and(eq("employeeId", entity.getEmployeeId()), eq("year", year),
							eq("leaveQuota.type", entity.getType().name())),
					combine(inc("leaveQuota.$.usedLeaves", -1 * entity.getNoOfDays()),
							inc("leaveQuota.$.approvalPendingLeaves", -1 * entity.getNoOfDays()),
							inc("leaveQuota.$.reameningLeaves", entity.getNoOfDays())));
			throw new PnsException("Insufficient Leaves!");
		}
		return findOneAndUpdate;
	}

}
