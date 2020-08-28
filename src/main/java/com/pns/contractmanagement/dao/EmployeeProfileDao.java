package com.pns.contractmanagement.dao;

import java.util.Optional;

import com.pns.contractmanagement.entity.EmployeeProfileEntity;

/**
 *
 */
public interface EmployeeProfileDao {

    EmployeeProfileEntity insert(EmployeeProfileEntity profile);

    Optional<EmployeeProfileEntity> getEmployeeProfileByEmployeeId(String username);

}
