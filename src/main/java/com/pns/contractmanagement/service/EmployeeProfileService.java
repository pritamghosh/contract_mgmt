package com.pns.contractmanagement.service;

import com.pns.contractmanagement.model.EmployeeProfile;

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

}
