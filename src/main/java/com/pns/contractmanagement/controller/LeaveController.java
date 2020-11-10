package com.pns.contractmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.model.ApproveRequest;
import com.pns.contractmanagement.model.HolidayCalendar;
import com.pns.contractmanagement.model.LeaveQuotaDetails;
import com.pns.contractmanagement.model.LeaveRequest;
import com.pns.contractmanagement.model.LeaveRequestDetails;
import com.pns.contractmanagement.service.LeaveService;

@RestController
@RequestMapping("/leave")
public class LeaveController {
	@Autowired
	private LeaveService service;

	@GetMapping("count")
	public long getCustomerbyid(@RequestParam("start") final String start, @RequestParam("end") final String end) {
		return service.countNoOfHolidays(start, end);
	}

	@GetMapping("calendar")
	public List<HolidayCalendar> getCustomerbyid() {
		return service.getHolidayCalendar();
	}

	@GetMapping("initialize")
	public boolean initialize(@RequestParam("employeeID") final String employeeID,
			@RequestParam(value = "year", defaultValue = "0") final int year) {
		return service.initializeLeaveQuota(employeeID, year);
	}

	@GetMapping("quota")
	public LeaveQuotaDetails getQuota(@RequestParam(required = false) final String employeeID,
			@RequestParam(value = "year", defaultValue = "0") final int year) {
		return service.getQuota(employeeID, year);
	}

	@GetMapping("history")
	public List<LeaveRequestDetails> getHistory(@RequestParam(value = "year", defaultValue = "0") final int year) {
		return service.getHistory(null, year);
	}

	@GetMapping("history/{employeeID}")
	public List<LeaveRequestDetails> getHistorybByEmployeeId(@PathVariable final String employeeID,
			@RequestParam(value = "year", defaultValue = "0") final int year) {
		return service.getHistory(employeeID, year);
	}

	@GetMapping("approve")
	public List<LeaveRequestDetails> geApprovalPendingList() {
		return service.geApprovalPendingList(null);
	}

	@PutMapping("approve")
	public LeaveRequestDetails geApprovalPendingList(@RequestBody ApproveRequest request) {
		return service.approve(request);
	}

	@GetMapping("approve/{employeeID}")
	public List<LeaveRequestDetails> geApprovalPendingListByEmployeeId(@PathVariable final String employeeID) {
		return service.geApprovalPendingList(employeeID);
	}

	@PostMapping("apply")
	public LeaveRequestDetails applyLeave(@RequestBody final LeaveRequest request) {
		return service.applyLeave(request);
	}
}
