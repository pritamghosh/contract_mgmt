package com.pns.contractmanagement.dao;

import java.util.Optional;

import com.pns.contractmanagement.entity.EmployeeProfileEntity;
import com.pns.contractmanagement.entity.SequenceEntity;

/**
 *
 */
public interface EmployeeProfileDao {

    EmployeeProfileEntity insert(EmployeeProfileEntity profile);

    Optional<EmployeeProfileEntity> findByEmployeeId(String username);
    
    SequenceEntity findAndUpdateSequece();
    
    Optional<EmployeeProfileEntity> findByEmail(String email);

}
