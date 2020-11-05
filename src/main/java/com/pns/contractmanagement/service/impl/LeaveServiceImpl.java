package com.pns.contractmanagement.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.pns.contractmanagement.dao.LeaveQuotaDao;
import com.pns.contractmanagement.entity.LeaveDetailsEntity;
import com.pns.contractmanagement.entity.LeaveDetailsEntity.LeaveQuotaEntity;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.HolidayHelperImpl;
import com.pns.contractmanagement.model.HolidayCalendar;
import com.pns.contractmanagement.model.ImmutableLeaveQuota;
import com.pns.contractmanagement.model.ImmutableLeaveQuotaDetails;
import com.pns.contractmanagement.model.LeaveQuotaDetails;
import com.pns.contractmanagement.model.LeaveRequest;
import com.pns.contractmanagement.model.LeaveType;
import com.pns.contractmanagement.service.EmployeeProfileService;
import com.pns.contractmanagement.service.LeaveService;
import com.pns.contractmanagement.util.ServiceUtil;

@Service
public class LeaveServiceImpl implements LeaveService {

	@Value("${app.leave.quota.total.cl}")
	private int totalCL;

	@Value("${app.leave.quota.total.pl}")
	private int totalPL;

	@Value("${app.leave.quota.total.sl}")
	private int totalSL;

	@Autowired
	private HolidayHelperImpl holidayHelper;

	@Autowired
	private LeaveQuotaDao leaveQuotaDao;

	@Autowired
	private EmployeeProfileService employeeService;

	@Override
	public boolean initializeLeaveQuota(final String employeeId, final int year) {
		if (employeeService.findProfileById(employeeId) == null) {
			throw new PnsException("No Employee found with ID " + employeeId);
		}
		final int i = year == 0 ? LocalDate.now().getYear() : year;

		final LocalDate form = LocalDate.of(i, 1, 1);
		final LocalDate to = LocalDate.of(i, 12, 31);
		final Optional<LeaveDetailsEntity> existingLeaveQuota = leaveQuotaDao.findLeaveQuota(employeeId, i);
		if (existingLeaveQuota.isEmpty()) {
			final List<LeaveQuotaEntity> leaveQuotaList = new ArrayList<LeaveDetailsEntity.LeaveQuotaEntity>();
			leaveQuotaList.add(new LeaveQuotaEntity(LeaveType.CL, totalCL, totalCL, 0, 0, form, to));
			leaveQuotaList.add(new LeaveQuotaEntity(LeaveType.PL, totalPL, totalPL, 0, 0, form, to));
			leaveQuotaList.add(new LeaveQuotaEntity(LeaveType.SL, totalSL, totalSL, 0, 0, form, to));
			leaveQuotaDao.insert(
					LeaveDetailsEntity.builder().employeeId(employeeId).year(i).leaveQuota(leaveQuotaList).build());
			return true;
		}
		throw new PnsException("Leave Quota is Already Initialized for " + employeeId);
	}

	@Override
	public LeaveRequest applyLeave(final LeaveRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRequest approve(final LeaveRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countNoOfHolidays(final String start, final String end) {
		final LocalDate startDate = LocalDate.parse(start);
		final LocalDate endDate = LocalDate.parse(end);
		final long diff = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
		final int countHolidays = holidayHelper.countHolidays(Range.closed(startDate, endDate),
				employeeService.getEmployeeRegion());
		return diff - countHolidays;
	}

	@Override
	public List<HolidayCalendar> getHolidayCalendar() {
		return holidayHelper.getHolidayCalendar();
	}

	@Override
	public LeaveQuotaDetails getQuota(final String employeeID, final int year) {
		final Optional<LeaveDetailsEntity> existingLeaveQuota = leaveQuotaDao.findLeaveQuota(
				employeeID != null ? employeeID : ServiceUtil.getUsernameFromContext(),
				year == 0 ? LocalDate.now().getYear() : year);
		if (existingLeaveQuota.isPresent()) {
			return map(existingLeaveQuota.get());
		}
		throw new PnsException("Leave Quota is not Initialized for " + employeeID);
	}

	private LeaveQuotaDetails map(LeaveDetailsEntity leaveDetailsEntity) {
		return ImmutableLeaveQuotaDetails.builder().employeeID(leaveDetailsEntity.getEmployeeId())
				.year(leaveDetailsEntity.getYear()).details(leaveDetailsEntity.getLeaveQuota().stream()
						// @formatter:off
						.map(e -> ImmutableLeaveQuota.builder().type(e.getType()).totalLeaves(e.getTotalLeaves())
								.reameningLeaves(e.getReameningLeaves()).usedLeaves(e.getUsedLeaves())
								.approvalPendingLeaves(e.getApprovalPendingLeaves())
								.deductableFrom(e.getDeductableFrom()).deductableTo(e.getDeductableTo()).build())
						// @formatter:on

						.sorted((e1, e2) -> e1.getType().compareTo(e2.getType())).collect(Collectors.toList()))
				.build();
	}

}
