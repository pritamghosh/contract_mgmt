package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.dao.SystemDao;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.helper.impl.ContractInvoiceHelperImpl;

/**
 *
 */
@RestController
@RequestMapping("/system")
public class SystemController {

	@Autowired
	private SystemDao dao;

	@Autowired
	private ContractInvoiceHelperImpl helper;

	@GetMapping("db")
	public boolean dbsetup() {
		dao.initIndexes();
		return true;
	}

	@GetMapping("init/sequence")
	public boolean initSequece(@RequestParam(value = "seq", defaultValue = "0") final int seq) {
		return dao.initSequece(seq);
	}

	@GetMapping("jasper/compile")
	public boolean buildJasper() throws PnsException {
		helper.compileJasper();
		return true;
	}
}
