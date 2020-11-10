package com.pns.contractmanagement.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.pns.contractmanagement.entity.LeaveRequestEntity;

public interface LeaveHistoryDao {

	List<LeaveRequestEntity> findLeaveHistory(long year);

	Optional<LeaveRequestEntity> findLeaveRequestById(String id);

	boolean approveOrRejectLeaveRequest(LeaveRequestEntity entity);

	LeaveRequestEntity insert(LeaveRequestEntity entity);

	List<LeaveRequestEntity> findHistoryByEmployeeId(String employeeID, LocalDate from, LocalDate to);

	List<LeaveRequestEntity> geApprovalPendingList(String employeeID);
}
