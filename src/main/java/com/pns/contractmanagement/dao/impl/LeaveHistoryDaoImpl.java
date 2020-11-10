/**
 *
 */
package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.time.LocalDate;
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
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.dao.LeaveHistoryDao;
import com.pns.contractmanagement.entity.LeaveRequestEntity;
import com.pns.contractmanagement.model.LeaveStatus;

/**
 * @author Pritam Ghosh
 *
 */
@Repository
public class LeaveHistoryDaoImpl implements LeaveHistoryDao {

	private final MongoCollection<LeaveRequestEntity> leaveHistotyCollection;

	@Autowired
	public LeaveHistoryDaoImpl(final MongoCollectionUtil util,
			final @Value("${app.index.name.employee.leaveHistory:leaveHistory}") String leaveHistoryIndex) {
		leaveHistotyCollection = util.getProfileCollection(leaveHistoryIndex, LeaveRequestEntity.class);
	}

	@Override
	public List<LeaveRequestEntity> findLeaveHistory(final long year) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequestEntity insert(final LeaveRequestEntity entity) {
		final InsertOneResult insertOne = leaveHistotyCollection.insertOne(entity);
		entity.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return entity;
	}

	@Override
	public List<LeaveRequestEntity> findHistoryByEmployeeId(final String employeeID, final LocalDate from,
			final LocalDate to) {
		final List<LeaveRequestEntity> list = new ArrayList<LeaveRequestEntity>();
		leaveHistotyCollection.find(and(eq("employeeId", employeeID), gte("from", from), lte("to", to))).iterator()
				.forEachRemaining(list::add);
		return list;
	}

	@Override
	public List<LeaveRequestEntity> geApprovalPendingList(final String employeeID) {
		final List<LeaveRequestEntity> list = new ArrayList<LeaveRequestEntity>();
		leaveHistotyCollection
				.find(and(eq("primaryApprover", employeeID),
						or(eq("status", LeaveStatus.PENDING.name()), eq("status", LeaveStatus.CANCEL_PENDING.name()))))
				.iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public Optional<LeaveRequestEntity> findLeaveRequestById(final String id) {
		return Optional.ofNullable(leaveHistotyCollection.find(new Document("_id", new ObjectId(id))).first());
	}

	@Override
	public boolean approveOrRejectLeaveRequest(final LeaveRequestEntity entity) {
		final Bson update = combine(
				// @formatter:off
				set("approvedOrRejectedBy", entity.getApprovedOrRejectedBy()),
				set("approvalOrRejectionDateTime", entity.getApprovalOrRejectionDateTime()),
				set("approvarNote", entity.getApprovarNote()), set("status", entity.getStatus().name())
		// @formatter:on
		);
		final UpdateResult ur = leaveHistotyCollection.updateOne(and(eq("_id", entity.getOid())), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}

}
