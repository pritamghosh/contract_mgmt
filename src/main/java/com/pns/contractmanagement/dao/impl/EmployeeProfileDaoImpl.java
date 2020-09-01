package com.pns.contractmanagement.dao.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.text;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity.Status;
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
	public Optional<EmployeeProfileEntity> findByEmployeeId(String username) {
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

	@Override
	public Optional<EmployeeProfileEntity> findByEmail(String email) {
		return Optional.ofNullable(employProfileCollection
				.find(new Document("workEmail", DaoUtil.buildCaseInsentiveQuery(email))).first());
	}

	@Override
	public boolean saveImage(String employeeId, byte[] image) {
		final Bson update = combine(set("image", image), set(DaoUtil.LAST_MODIFIED_BY, employeeId),
				set(DaoUtil.LAST_MODIFIED_DATE, LocalDateTime.now()));
		final UpdateResult ur = employProfileCollection.updateOne(eq("employeeId", employeeId), update);
		return ur.getMatchedCount() > 0 && ur.getModifiedCount() > 0;
	}
	@Override
	public List<EmployeeProfileEntity> searchByQuery(final String query) {
        final List<EmployeeProfileEntity> employeeProfiles = new ArrayList<>();
        employProfileCollection.find(and(text(query),  new Document("status", Status.ACTIVE.name())))
                .iterator().forEachRemaining(employeeProfiles::add);
        return employeeProfiles;
    }

}
