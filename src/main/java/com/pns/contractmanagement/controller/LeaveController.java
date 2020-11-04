package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
