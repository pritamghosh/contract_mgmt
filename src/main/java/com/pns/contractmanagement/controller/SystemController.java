package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.dao.impl.SystemDaoImpl;
import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;

/**
 *
 */
@RestController
@RequestMapping("/system")
public class SystemController {

	@Autowired
	private SystemDaoImpl dao;

	@Autowired
	private ContractInvoiceHelperImpl helper;


	@GetMapping("db")
	@PreAuthorize("hasRole('system')")
	public boolean dbsetup() {
		dao.initIndexes();
		return true;
	}

	@GetMapping("init/sequence/proposal")
	@PreAuthorize("hasRole('system')")
	public boolean initProposalNoSequence(@RequestParam(value = "seq", defaultValue = "0") final int seq) {
		return dao.initProposalNoSequence(seq);
	}
	
	@GetMapping("init/sequence/employee")
	@PreAuthorize("hasRole('system')")
	public boolean initEmployeeNoSequence(@RequestParam(value = "seq", defaultValue = "0") final int seq) {
		return dao.initEmployeeNoSequence(seq);
	}

	@GetMapping("jasper/compile")
	@PreAuthorize("hasRole('system')")
	public boolean buildJasper() {
		helper.compileJasper();
		return true;
	}
	
}
