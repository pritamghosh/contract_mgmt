package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.impl.CustomerServiceImpl;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerServiceImpl service;

	@PutMapping
	public Customer addCustomer(@RequestBody final Customer customer) throws PnsException {
		return service.addCustomer(customer);
	}

	@PostMapping
	public Customer modifyCustomer(@RequestBody final Customer Customer) throws PnsException {
		return service.ModifyCustomer(Customer);
	}

	@DeleteMapping("/{id}")
	public Customer DeleteCustomerById(@PathVariable("id") final String id) throws PnsException {
		return service.DeleteCustomerById(id);
	}

	@GetMapping("{id}")
	public Customer getCustomerbyid(@PathVariable("id") final String id) throws PnsException {
		return service.getCustomerbyid(id);
	}

	@GetMapping(params = { "region", "!name" })
	public SearchResponse<Customer> getCustomerbyRegion(@RequestParam("region") final String region,
			@RequestParam(value = "page", defaultValue = "1") final int page) throws PnsException {
		return service.getCustomerByRegion(region,page);
	}

	@GetMapping(params = { "name", "!region" })
	public SearchResponse<Customer> getCustomerbyName(@RequestParam("name") final String name,
			@RequestParam(value = "page", defaultValue = "1") final int page) throws PnsException {
		return service.getCustomerByName(name,page);
	}

	@GetMapping(value = "search", params = { "query" })
	public SearchResponse<Customer> searchCustomerbyQuery(@RequestParam("query") final String query,
			@RequestParam(value = "page", defaultValue = "1") final int page) throws PnsException {
		return service.searchCustomerbyQuery(query,page);
	}

	@GetMapping(params = { "!region", "!name" })
	public SearchResponse<Customer> getAllCustomer(@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getAllCustomer(page);
	}
}
