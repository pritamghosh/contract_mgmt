/**
 * 
 */
package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.dao.LeaveHistoryDao;
import com.pns.contractmanagement.entity.LeaveRequestEntity;

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
	public List<LeaveRequestEntity> findLeaveHistory(long year) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequestEntity insert(LeaveRequestEntity entity) {
		final InsertOneResult insertOne = leaveHistotyCollection.insertOne(entity);
		entity.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return entity;
	}

	@Override
	public List<LeaveRequestEntity> findHistoryByEmployeeId(String employeeID, LocalDate from, LocalDate to) {
		List<LeaveRequestEntity> list = new ArrayList<LeaveRequestEntity>();
		leaveHistotyCollection.find(and(eq("employeeId", employeeID), gte("from", from), lte("to", to))).iterator()
				.forEachRemaining(list::add);
		return list;
	}

}
