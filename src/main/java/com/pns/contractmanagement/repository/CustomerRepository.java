package com.pns.contractmanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pns.contractmanagement.model.Customer;


public interface CustomerRepository extends MongoRepository<Customer, Long> {
}
