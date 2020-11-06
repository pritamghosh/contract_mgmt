package com.pns.contractmanagement.dao;

import java.util.Optional;

import com.pns.contractmanagement.entity.LeaveDetailsEntity;
import com.pns.contractmanagement.entity.LeaveRequestEntity;

public interface LeaveQuotaDao {

	Optional<LeaveDetailsEntity> findLeaveQuota(String employeeId, long year);

	LeaveDetailsEntity insert(LeaveDetailsEntity entity);
	
	LeaveDetailsEntity updateLeaveQuota(LeaveRequestEntity entity, int year);

}
