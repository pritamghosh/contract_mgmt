package com.pns.contractmanagement.service;

import java.util.List;

import com.pns.contractmanagement.model.ApproveRequest;
import com.pns.contractmanagement.model.HolidayCalendar;
import com.pns.contractmanagement.model.LeaveQuotaDetails;
import com.pns.contractmanagement.model.LeaveRequest;
import com.pns.contractmanagement.model.LeaveRequestDetails;

/**
 *
 */
public interface LeaveService {
    boolean initializeLeaveQuota(String employeeId, int year);
    
    LeaveRequestDetails applyLeave(LeaveRequest request);
    
    
    
    LeaveRequestDetails approve(ApproveRequest request);

	long countNoOfHolidays(String start, String end);

	List<HolidayCalendar> getHolidayCalendar();

	LeaveQuotaDetails getQuota(String employeeID, int year);
	
	List<LeaveRequestDetails> getHistory(String employeeID, int year);

	List<LeaveRequestDetails> geApprovalPendingList(String employeeID);
}
