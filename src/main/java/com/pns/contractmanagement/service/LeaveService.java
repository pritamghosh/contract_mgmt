package com.pns.contractmanagement.service;

import java.util.List;

import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.HolidayCalendar;
import com.pns.contractmanagement.model.LeaveRequest;

/**
 *
 */
public interface LeaveService {
    boolean initializeLeaveQuota(EmployeeProfile profile);
    
    LeaveRequest applyLeave(LeaveRequest request);
    
    LeaveRequest approve(LeaveRequest request);

	long countNoOfHolidays(String start, String end);

	List<HolidayCalendar> getHolidayCalendar();
}
