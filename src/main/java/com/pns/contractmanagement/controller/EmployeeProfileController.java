package com.pns.contractmanagement.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pns.contractmanagement.model.EmployeeProfile;
import com.pns.contractmanagement.service.EmployeeProfileService;

/**
 *
 */
@RestController
@RequestMapping("/employee/profile")
public class EmployeeProfileController {
	@Autowired
	private EmployeeProfileService service;

	@PostMapping
	public EmployeeProfile createEmployeeProfie(@RequestBody final EmployeeProfile employeeProfile) {
		return service.createEmployeeProfile(employeeProfile);
	}

	@GetMapping
	public EmployeeProfile getEmployeeProfile() {
		return service.getEmployeeProfile();
	}
	
	@PatchMapping("image")
	public EmployeeProfile uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
		return service.uploadImage(image.getBytes());
	}
}
