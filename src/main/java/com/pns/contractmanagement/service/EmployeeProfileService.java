package com.pns.contractmanagement.service;

import java.util.List;

import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.Manager;

/**
 *
 */
public interface EmployeeProfileService {

    /**
     * @param employeeProfile
     * @return
     */
    EmployeeProfile createEmployeeProfile(EmployeeProfile employeeProfile);

    /**
     * @return
     */
    EmployeeProfile getEmployeeProfile();

	EmployeeProfile uploadImage(byte[] image);

    /**
     * @param id
     * @return
     */
    EmployeeProfile findProfileById(String id);

    /**
     * @param query
     * @return
     */
    List<Manager> searchManager(String query);

}
