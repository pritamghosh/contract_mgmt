package com.pns.contractmanagement.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
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
import com.pns.contractmanagement.model.ApproveRequest;
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

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	private final String leaveRequestTemplateText;

	private final String leaveRequestApproveTemplateText;

	@Value("${app.leave.approve.url}")
	private String approveUrl;
	
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

	public LeaveServiceImpl() throws IOException {
		final InputStream stream = this.getClass().getResourceAsStream("/templates/leaveRequestTemplate.html");
		leaveRequestTemplateText = IOUtils.toString(stream, Charset.defaultCharset());
		leaveRequestApproveTemplateText = IOUtils.toString(
				this.getClass().getResourceAsStream("/templates/leaveRequestActionedTemplate.html"),
				Charset.defaultCharset());
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
		final EmployeeProfile employeeProfile = employeeService.getEmployeeProfile();
		final String employeeName = new StringBuilder().append(employeeProfile.getFirstName()).append(" ")
				.append(employeeProfile.getFamilyName()).toString();
		final int year = LocalDate.now().getYear();
		final LeaveRequestEntity entity = LeaveRequestEntity.builder().dateTimeOfApply(LocalDateTime.now())
				.employeeId(employeeID).from(request.getFrom()).to(request.getTo()).noOfDays(noOfHolidays)
				.note(request.getNote()).primaryApprover(request.getPrimaryApprover()).status(LeaveStatus.PENDING)
				.employeeName(employeeName).type(request.getType()).build();
		leaveQuotaDao.updateLeaveQuotaForApplyingLeave(entity, year);
		final LeaveRequestEntity inserted = leaveHistoryDao.insert(entity);
		final DelegatingSecurityContextRunnable runnableMailSender = new DelegatingSecurityContextRunnable(
				() -> sendMailToApprover(inserted, employeeProfile));
		threadPool.submit(runnableMailSender);
		return map(inserted);
	}

	private LeaveRequestDetails map(final LeaveRequestEntity entity) {
		return ImmutableLeaveRequestDetails.builder()
				.approvalOrRejectionDateTime(entity.getApprovalOrRejectionDateTime())
				.approvarNote(entity.getApprovarNote()).approvedOrRejectedBy(entity.getApprovedOrRejectedBy())
				.dateTimeOfApply(entity.getDateTimeOfApply()).employeeId(entity.getEmployeeId()).from(entity.getFrom())
				.to(entity.getTo()).noOfDays(entity.getNoOfDays()).note(entity.getNote())
				.primaryApprover(entity.getPrimaryApprover()).status(entity.getStatus()).type(entity.getType())
				.employeeName(entity.getEmployeeName()).id(entity.getId()).build();
	}

	private void sendMailToApprover(final LeaveRequestEntity entity, final EmployeeProfile employeeProfile) {

		final EmployeeProfile rmProfile = employeeService.findProfileById(entity.getPrimaryApprover());
		final Map<CharSequence, CharSequence> keyReplacementMap = populateLKeyReplacementMap(entity, employeeProfile);
		final MailVo mailVO = MailVo.builder().toList(List.of(rmProfile.getWorkEmail()))
				.ccList(List.of(employeeProfile.getWorkEmail())).subject("Leave Request")
				.htmlText(populateHtmlString(leaveRequestTemplateText, keyReplacementMap))
				.form("Leave.System@pnsservices.com").build();
		mailServiceHelper.sendMail(mailVO);
	}

	private Map<CharSequence, CharSequence> populateLKeyReplacementMap(final LeaveRequestEntity entity,
			final EmployeeProfile employeeProfile) {
		final Map<CharSequence, CharSequence> keyReplacementMap = new HashMap<>();
		keyReplacementMap.put("${employee.rm}", employeeProfile.getReportingManager().getName());
		keyReplacementMap.put("${employee.lname}", employeeProfile.getFamilyName());
		keyReplacementMap.put("${employee.fname}", employeeProfile.getFirstName());
		keyReplacementMap.put("${employee.id}", employeeProfile.getEmployeeId());
		keyReplacementMap.put("${from}", entity.getFrom().format(DateTimeFormatter.ISO_LOCAL_DATE));
		keyReplacementMap.put("${to}", entity.getTo().format(DateTimeFormatter.ISO_LOCAL_DATE));
		keyReplacementMap.put("${noOfDays}", String.valueOf(entity.getNoOfDays()));
		keyReplacementMap.put("${type}", entity.getType().getValue());
		keyReplacementMap.put("${noOfDays}", employeeProfile.getReportingManager().getName());
		keyReplacementMap.put("${note}", entity.getNote());
		keyReplacementMap.put("${approverNote}", entity.getApprovarNote());
		keyReplacementMap.put("${action}", entity.getStatus().getAction());
		keyReplacementMap.put("${actionlink}", approveUrl);
		return keyReplacementMap;
	}

	private String populateHtmlString(final String template, final Map<CharSequence, CharSequence> keyReplacementMap) {
		final Iterator<Entry<CharSequence, CharSequence>> keyMapIterator = keyReplacementMap.entrySet().iterator();
		String htmlString = template;
		while (keyMapIterator.hasNext()) {
			final Entry<CharSequence, CharSequence> next = keyMapIterator.next();
			htmlString = htmlString.replace(next.getKey(), next.getValue() != null ? next.getValue() : "");
		}
		return htmlString;
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

	@Override
	public List<LeaveRequestDetails> geApprovalPendingList(final String employeeID) {
		return leaveHistoryDao
				.geApprovalPendingList(employeeID != null ? employeeID : ServiceUtil.getUsernameFromContext()).stream()
				.map(this::map).sorted((e1, e2) -> e1.getDateTimeOfApply().compareTo(e2.getDateTimeOfApply()))
				.collect(Collectors.toList());
	}

	@Override
	public LeaveRequestDetails approve(final ApproveRequest request) {
		final Optional<LeaveRequestEntity> existingLRResp = leaveHistoryDao.findLeaveRequestById(request.getId());
		if (existingLRResp.isEmpty()) {
			throw new PnsException("Unbale to fine any Leave Request");
		}
		final LeaveRequestEntity leaveRequest = existingLRResp.get();
		if (!ServiceUtil.getUsernameFromContext().equalsIgnoreCase(leaveRequest.getPrimaryApprover())) {
			throw new PnsException("Invalid Approver");
		}
		if (LeaveStatus.PENDING == leaveRequest.getStatus()) {
			if (request.getAction()) {
				leaveRequest.setStatus(LeaveStatus.APPROVED);
				leaveQuotaDao.updateLeaveQuotaForApprovingLeave(leaveRequest, leaveRequest.getFrom().getYear());
			} else {
				leaveRequest.setStatus(LeaveStatus.REJECTED);
			}
		} else if (LeaveStatus.CANCEL_PENDING == leaveRequest.getStatus()) {
			if (request.getAction()) {
				leaveRequest.setStatus(LeaveStatus.CANCELLED);
				leaveQuotaDao.updateLeaveQuotaForCancellingLeave(leaveRequest, leaveRequest.getFrom().getYear());
			} else {
				leaveRequest.setStatus(LeaveStatus.CANCEL_REJECTED);
			}
		} else {
			throw new PnsException("Unable to Approve/Reject Leave Request");
		}

		leaveRequest.setApprovalOrRejectionDateTime(LocalDateTime.now());
		leaveRequest.setApprovarNote(request.getComment());
		leaveRequest.setApprovedOrRejectedBy(ServiceUtil.getUsernameFromContext());
		leaveHistoryDao.approveOrRejectLeaveRequest(leaveRequest);
		final DelegatingSecurityContextRunnable runnableMailSender = new DelegatingSecurityContextRunnable(
				() -> sendMailOnLeaveRequestAction(leaveRequest));
		threadPool.submit(runnableMailSender);
		return map(leaveRequest);
	}

	private void sendMailOnLeaveRequestAction(final LeaveRequestEntity entity) {
		final EmployeeProfile employeeProfile = employeeService.findProfileById(entity.getEmployeeId());
		final EmployeeProfile rmProfile = employeeService.getEmployeeProfile();
		final Map<CharSequence, CharSequence> keyReplacementMap = populateLKeyReplacementMap(entity, employeeProfile);
		final MailVo mailVO = MailVo.builder().toList(List.of(employeeProfile.getWorkEmail()))
				.ccList(List.of(rmProfile.getWorkEmail())).subject("Leave Request " + entity.getStatus().getAction())
				.htmlText(populateHtmlString(leaveRequestApproveTemplateText, keyReplacementMap))
				.form("Leave.System@pnsservices.com").build();
		mailServiceHelper.sendMail(mailVO);
	}

}
