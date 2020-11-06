package com.pns.contractmanagement.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.pns.contractmanagement.dao.LeaveHistoryDao;
import com.pns.contractmanagement.dao.LeaveQuotaDao;
import com.pns.contractmanagement.entity.LeaveDetailsEntity;
import com.pns.contractmanagement.entity.LeaveDetailsEntity.LeaveQuotaEntity;
import com.pns.contractmanagement.entity.LeaveRequestEntity;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.HolidayHelperImpl;
import com.pns.contractmanagement.helper.impl.MailServiceHelperImpl;
import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.model.HolidayCalendar;
import com.pns.contractmanagement.model.ImmutableLeaveQuota;
import com.pns.contractmanagement.model.ImmutableLeaveQuotaDetails;
import com.pns.contractmanagement.model.ImmutableLeaveRequestDetails;
import com.pns.contractmanagement.model.LeaveQuotaDetails;
import com.pns.contractmanagement.model.LeaveRequest;
import com.pns.contractmanagement.model.LeaveRequestDetails;
import com.pns.contractmanagement.model.LeaveStatus;
import com.pns.contractmanagement.model.LeaveType;
import com.pns.contractmanagement.service.EmployeeProfileService;
import com.pns.contractmanagement.service.LeaveService;
import com.pns.contractmanagement.util.ServiceUtil;
import com.pns.contractmanagement.vo.MailVo;

@Service
public class LeaveServiceImpl implements LeaveService {

	private final String leavetemplateText;

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
	private LeaveHistoryDao leaveHistoryDao;

	@Autowired
	private EmployeeProfileService employeeService;

	@Autowired
	private MailServiceHelperImpl mailServiceHelper;

	public LeaveServiceImpl() throws IOException, URISyntaxException {
		leavetemplateText = Files
				.readString(Paths.get(getClass().getResource("/templates/leaveRequestTemplate.html").toURI()));
	}

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
	public LeaveRequestDetails applyLeave(final LeaveRequest request) {
		final long noOfHolidays = countNoOfHolidays(request.getFrom(), request.getTo());
		if (noOfHolidays < 1 || (request.getFrom().getYear() != request.getTo().getYear())) {
			throw new PnsException("Invalid Date Range!");
		}
		final String employeeID = ServiceUtil.getUsernameFromContext();
		final int year = LocalDate.now().getYear();
		final LeaveRequestEntity entity = LeaveRequestEntity.builder().dateTimeOfApply(LocalDateTime.now())
				.employeeId(employeeID).from(request.getFrom()).to(request.getTo()).noOfDays(noOfHolidays)
				.note(request.getNote()).primaryApprover(request.getPrimaryApprover()).status(LeaveStatus.PENDING)
				.type(request.getType()).build();
		leaveQuotaDao.updateLeaveQuota(entity, year);
		final LeaveRequestEntity inserted = leaveHistoryDao.insert(entity);
		sendMailToApprover(inserted);
		return map(inserted);
	}

	private LeaveRequestDetails map(final LeaveRequestEntity entity) {
		return ImmutableLeaveRequestDetails.builder().approvalDateTime(entity.getApprovalDateTime())
				.approvarNote(entity.getApprovarNote()).approvedBy(entity.getApprovedBy())
				.dateTimeOfApply(entity.getDateTimeOfApply()).employeeId(entity.getEmployeeId()).from(entity.getFrom())
				.to(entity.getTo()).noOfDays(entity.getNoOfDays()).note(entity.getNote())
				.primaryApprover(entity.getPrimaryApprover()).status(entity.getStatus()).type(entity.getType())
				.id(entity.getId()).build();
	}

	private void sendMailToApprover(final LeaveRequestEntity entity) {
		final EmployeeProfile employeeProfile = employeeService.getEmployeeProfile();
		final EmployeeProfile rmProfile = employeeService
				.findProfileById(employeeProfile.getReportingManager().getEmployeeId());

		final String htmlText = leavetemplateText.replace("${name}", employeeProfile.getReportingManager().getName())
				.replace("${employee.lname}", employeeProfile.getFamilyName())
				.replace("${employee.fname}", employeeProfile.getFirstName())
				.replace("${employee.id}", employeeProfile.getEmployeeId())
				.replace("${form}", entity.getFrom().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.replace("${to}", entity.getTo().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.replace("${noOfDays}", String.valueOf(entity.getNoOfDays()))
				.replace("${type}", entity.getType().getValue()).replace("${note}", entity.getNote())

		;

		mailServiceHelper.sendMail(MailVo.builder().toList(List.of(rmProfile.getWorkEmail()))
				.ccList(List.of(employeeProfile.getWorkEmail())).subject("Leave Request").htmlText(htmlText)
				.form("Leave.System@pnsservices.com").build());
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
		return countNoOfHolidays(startDate, endDate);
	}

	private long countNoOfHolidays(final LocalDate startDate, final LocalDate endDate) {
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

	private LeaveQuotaDetails map(final LeaveDetailsEntity leaveDetailsEntity) {
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

	@Override
	public List<LeaveRequestDetails> getHistory(final String employeeID, final int year) {
		final String employeeId = employeeID != null ? employeeID : ServiceUtil.getUsernameFromContext();
		if (employeeService.findProfileById(employeeId) == null) {
			throw new PnsException("No Employee found with ID " + employeeId);
		}
		final int i = year == 0 ? LocalDate.now().getYear() : year;

		final LocalDate from = LocalDate.of(i, 1, 1);
		final LocalDate to = LocalDate.of(i, 12, 31);
		final List<LeaveRequestEntity> responseFromDB = leaveHistoryDao.findHistoryByEmployeeId(employeeId, from, to);
		return responseFromDB.stream().map(this::map)
				.sorted((e1, e2) -> e2.getDateTimeOfApply().compareTo(e1.getDateTimeOfApply()))
				.collect(Collectors.toList());
	}

}
