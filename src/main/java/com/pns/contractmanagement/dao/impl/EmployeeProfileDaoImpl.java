package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Updates.inc;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.SequenceEntity;
import com.pns.contractmanagement.util.DaoUtil;

/**
 * Implementation of {@link EmployeeProfileDao} interface.
 */
@Repository
public class EmployeeProfileDaoImpl implements EmployeeProfileDao {

	private static final String SQUENCE_TYPE = "squenceType";
	private static final String EMPLOYEE_NO = "employee-no";
	private final MongoCollection<EmployeeProfileEntity> employProfileCollection;
	private final MongoCollection<SequenceEntity> sequenceDocumentCollection;

	@Autowired
	public EmployeeProfileDaoImpl(final MongoCollectionUtil util,
			final @Value("${app.index.name.employee.profile:employeeProfiles}") String employeeFrofileIndex,
			final @Value("${app.index.name.sequence:sequences}") String sequenceIndexName) {
		employProfileCollection = util.getProfileCollection(employeeFrofileIndex, EmployeeProfileEntity.class);
		sequenceDocumentCollection = util.getProfileCollection(sequenceIndexName, SequenceEntity.class);
	}

	/** {@inheritDoc} */
	@Override
	public EmployeeProfileEntity insert(EmployeeProfileEntity profile) {
		DaoUtil.setCreationDetails(profile);
		final InsertOneResult insertOne = employProfileCollection.insertOne(profile);
		profile.setOid(insertOne.getInsertedId().asObjectId().getValue());
		return profile;
	}

	/** {@inheritDoc} */
	@Override
	public Optional<EmployeeProfileEntity> getEmployeeProfileByEmployeeId(String username) {
		return Optional.ofNullable(employProfileCollection.find(new Document("employeeId", username)).first());
	}

	public SequenceEntity findAndUpdateSequece() {
		SequenceEntity sequence = sequenceDocumentCollection.findOneAndUpdate(new Document(SQUENCE_TYPE, EMPLOYEE_NO),
				inc("sequence", 1));
		if (sequence == null) {
			sequence = SequenceEntity.builder().sequence(1).squenceType(EMPLOYEE_NO).build();
			sequenceDocumentCollection.insertOne(sequence);
		} else {
			sequence.setSequence(sequence.getSequence() + 1);
			return sequence;
		}
		return sequence;

	}

}
