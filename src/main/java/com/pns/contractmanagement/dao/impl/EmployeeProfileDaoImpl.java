package com.pns.contractmanagement.dao.impl;

import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.pns.contractmanagement.dao.EmployeeProfileDao;
import com.pns.contractmanagement.entity.CustomerEntity;
import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.util.DaoUtil;

/**
 *Implementation of {@link EmployeeProfileDao} interface.
 */
public class EmployeeProfileDaoImpl implements EmployeeProfileDao {
    
    private final MongoCollection<EmployeeProfileEntity> employProfileCollection;
    
    @Autowired
    public EmployeeProfileDaoImpl(final MongoCollectionUtil util,
            final @Value("${app.index.name.employee.profile:employeeProfiles}") String employeeFrofileIndex) {
        employProfileCollection = util.getContractCollection(employeeFrofileIndex, EmployeeProfileEntity.class);
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

}
