package com.pns.contractmanagement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.service.impl.CustomerServiceImpl;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerServiceImpl service;

    @PutMapping
    public Customer addCustomer(final Customer customer) throws PnsException {
        return service.addCustomer(customer);
    }

    @PostMapping
    public Customer modifyCustomer(final Customer Customer) throws PnsException {
        return service.ModifyCustomer(Customer);
    }

    @DeleteMapping("/{id}")
    public Customer DeleteCustomerById(@PathVariable("id") final long id) throws PnsException {
        return service.DeleteCustomerById(id);
    }

    @GetMapping("{id}")
    public Customer getCustomerbyid(@PathVariable("id") final long id) throws PnsException {
        return service.getCustomerbyid(id);
    }

    @GetMapping

    public List<Customer> getAllCustomer() {
        return service.getAllCustomer();
    }
}
