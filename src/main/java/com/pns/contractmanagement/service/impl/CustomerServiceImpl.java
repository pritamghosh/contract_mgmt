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

@Service
public class CustomerServiceImpl {

    @Autowired CustomerDao dao;

    public Customer addCustomer(final Customer customer) throws PnsException {
        return map(dao.insert(map(customer)));
    }
    public Customer ModifyCustomer(final Customer customer) throws PnsException {
        dao.update(map(customer));
        return getCustomerbyid(customer.getId());
    }


    public Customer DeleteCustomerById( final String id) throws PnsException {
        final Customer deletedCustomer = getCustomerbyid(id);
        dao.deleteById(id);
        return deletedCustomer;
    }

    public Customer getCustomerbyid( final String id) throws PnsException {
        return map(dao.findById(id).orElseThrow(()->new PnsException("Customer Not Found!!",PnsError.NOT_FOUND)));
    }
    
    public List<Customer> getCustomerByRegion(final String region) {
        return map(dao.findByRegion(region));
    }
    
    public List<Customer> getCustomerByName(String name) {
        return map(dao.findByName(name));
    }
    
    public List<Customer> searchCustomerbyQuery(final String query) {
        return map(dao.searchByQuery(query));
    }
    public List<Customer> getAllCustomer() {
        return map(dao.findAll());
    }
    
    
    private List<Customer> map(Collection<CustomerEntity> list){
        return list.stream().map(e->map(e)).collect(Collectors.toList());
    }
    
    private CustomerEntity map(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setName(customer.getName());
        entity.setRegion(customer.getRegion());
        return entity;
    }
    
    private Customer map(CustomerEntity customer) {
        return ImmutableCustomer.builder().id(customer.getId()).name(customer.getName()).region(customer.getRegion()).build();
    }

}
