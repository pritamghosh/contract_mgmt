package com.pns.contractmanagement.dao;

import java.util.List;
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

	boolean saveImage(String employeeId, byte[] image);

    /**
     * @param query
     * @return
     */
    List<EmployeeProfileEntity> searchByQuery(String query);

    List<EmployeeProfileEntity> findAll(final int page);

	List<EmployeeProfileEntity> searchByQuery(String query, int page);

	long countAllDocumnets();

	long countAllDocumnets(String query);

}
