package com.pns.contractmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public List<LeaveRequestDetails> getHistory(@RequestParam(required = false) final String employeeID,
			@RequestParam(value = "year", defaultValue = "0") final int year) {
		return service.getHistory(employeeID, year);
	}

	@PostMapping("apply")
	public LeaveRequestDetails applyLeave(@RequestBody final LeaveRequest leaveRequest) {
		return service.applyLeave(leaveRequest);
	}
}
