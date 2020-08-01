package com.pns.contractmanagement.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.repository.CustomerRepository;

@Service
public class CustomerServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    @Autowired CustomerRepository repository;

    public Customer addCustomer(final Customer customer) throws PnsException {
        if(!repository.findById(customer.getId()).isPresent()) {
            return repository.save(customer);
        }
        throw new PnsException("Customer is already present with same details",PnsError.DUPLICTE_RECORD);
    }
    
    public Customer ModifyCustomer(final Customer customer) throws PnsException {
        getCustomerbyid(customer.getId());
        return repository.save(customer);
    }


    public Customer DeleteCustomerById( final long id) throws PnsException {
        final Customer deletedCustomer = getCustomerbyid(id);
        repository.deleteById(id);
        return deletedCustomer;
    }

    public Customer getCustomerbyid( final long id) throws PnsException {
        return repository.findById(id).orElseThrow(()->new PnsException("Customer Not Found!!",PnsError.NOT_FOUND));
    }

    public List<Customer> getAllCustomer() {
        return repository.findAll();
    }
}
