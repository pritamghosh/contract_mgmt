package com.pns.contractmanagement.dao;

import java.util.Optional;

import com.pns.contractmanagement.entity.LeaveDetailsEntity;

public interface LeaveQuotaDao {

	Optional<LeaveDetailsEntity> findLeaveQuota(String employeeId, long year);

	LeaveDetailsEntity insert(LeaveDetailsEntity entity);

}
