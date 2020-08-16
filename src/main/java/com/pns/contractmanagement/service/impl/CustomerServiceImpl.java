package com.pns.contractmanagement.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.CustomerDao;
import com.pns.contractmanagement.entity.CustomerEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.ImmutableCustomer;
import com.pns.contractmanagement.model.ImmutableSearchResponse;
import com.pns.contractmanagement.model.SearchResponse;

@Service
public class CustomerServiceImpl {

	@Autowired
	private CustomerDao dao;

	public Customer addCustomer(final Customer customer) throws PnsException {
		return map(dao.insert(map(customer)));
	}

	public Customer ModifyCustomer(final Customer customer) throws PnsException {
		dao.update(map(customer));
		return getCustomerbyid(customer.getId());
	}

	public Customer DeleteCustomerById(final String id) throws PnsException {
		final Customer deletedCustomer = getCustomerbyid(id);
		dao.deleteById(id);
		return deletedCustomer;
	}

	public Customer getCustomerbyid(final String id) throws PnsException {
		return map(dao.findById(id).orElseThrow(() -> new PnsException("Customer Not Found!!", PnsError.NOT_FOUND)));
	}

	public SearchResponse<Customer> getCustomerByRegion(final String region, int page) {
		return ImmutableSearchResponse.<Customer>builder().result(map(dao.findByRegion(region,page)))
				.pageCount(dao.countDocumnetsByRegion(region)).build();
	}

	public SearchResponse<Customer> getCustomerByName(String name, int page) {
		return ImmutableSearchResponse.<Customer>builder().result(map(dao.findByName(name,page)))
				.pageCount(dao.countDocumnetsByName(name)).build();
	}

	public SearchResponse<Customer> searchCustomerbyQuery(final String query, int page) {
		return ImmutableSearchResponse.<Customer>builder().result(map(dao.searchByQuery(query,page)))
				.pageCount(dao.countDocumnetsByQuery(query)).build();
	}

	public SearchResponse<Customer> getAllCustomer(final int page) {
		return ImmutableSearchResponse.<Customer>builder().result(map(dao.findAll(page)))
				.pageCount(dao.countAllDocumnets()).build();
	}

	private List<Customer> map(Collection<CustomerEntity> list) {
		return list.stream().map(e -> map(e)).collect(Collectors.toList());
	}

	private CustomerEntity map(Customer customer) {
		CustomerEntity entity = CustomerEntity.builder().name(customer.getName()).region(customer.getRegion())
				.gstinNo(customer.getGstinNo()).address(customer.getAddress()).pan(customer.getPan()).build();
		entity.setId(customer.getId());
		return entity;
	}

	private Customer map(CustomerEntity customer) {
		return ImmutableCustomer.builder().id(customer.getId()).name(customer.getName()).region(customer.getRegion())
				.gstinNo(customer.getGstinNo()).address(customer.getAddress()).pan(customer.getPan()).build();
	}

}
