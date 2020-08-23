package com.pns.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.impl.CustomerServiceImpl;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerServiceImpl service;

	@PutMapping
	@PreAuthorize("hasRole('create')")
	public Customer addCustomer(@RequestBody final Customer customer) {
		return service.addCustomer(customer);
	}

	@PostMapping
	@PreAuthorize("hasRole('update')")
	public Customer modifyCustomer(@RequestBody final Customer customer) {
		return service.modifyCustomer(customer);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('delete')")
	public Customer deleteCustomerById(@PathVariable("id") final String id) {
		return service.deleteCustomerById(id);
	}

	@GetMapping("{id}")
	@PreAuthorize("hasRole('read')")
	public Customer getCustomerbyid(@PathVariable("id") final String id) {
		return service.getCustomerbyid(id);
	}

	@GetMapping(params = { "region", "!name" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Customer> getCustomerbyRegion(@RequestParam("region") final String region,
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getCustomerByRegion(region, page);
	}

	@GetMapping(params = { "name", "!region" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Customer> getCustomerbyName(@RequestParam("name") final String name,
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getCustomerByName(name, page);
	}

	@GetMapping(value = "search", params = { "query" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Customer> searchCustomerbyQuery(@RequestParam("query") final String query,
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.searchCustomerbyQuery(query, page);
	}

	@GetMapping(params = { "!region", "!name" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Customer> getAllCustomer(@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getAllCustomer(page);
	}
}
