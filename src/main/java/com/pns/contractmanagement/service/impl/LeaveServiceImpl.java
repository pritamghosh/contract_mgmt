package com.pns.contractmanagement.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.pns.contractmanagement.helper.impl.HolidayHelperImpl;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.LeaveRequest;
import com.pns.contractmanagement.service.LeaveService;

@Service
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	private HolidayHelperImpl holidayHelper = new HolidayHelperImpl("/holiday.xlsx");

	@Override
	public boolean initializeLeaveQuota(EmployeeProfile profile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LeaveRequest applyLeave(LeaveRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequest approve(LeaveRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countNoOfHolidays(String start, String end) {
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		final long diff = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
		final int countHolidays = holidayHelper.countHolidays(Range.closed(startDate, endDate), "Kolkata");
		return diff - countHolidays;
	}

	public static void main(String[] args) {
		LeaveServiceImpl impl = new LeaveServiceImpl();
		final long countNoOfHolidays = impl.countNoOfHolidays("2020-01-01", "2020-01-31");
		System.out.println(countNoOfHolidays);
	}

}
