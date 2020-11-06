package com.pns.contractmanagement.dao;

import java.time.LocalDate;
import java.util.List;

import com.pns.contractmanagement.entity.LeaveRequestEntity;

public interface LeaveHistoryDao {

	List<LeaveRequestEntity> findLeaveHistory(long year);

	LeaveRequestEntity insert(LeaveRequestEntity entity);

	List<LeaveRequestEntity> findHistoryByEmployeeId(String employeeID, LocalDate from, LocalDate to);
}
