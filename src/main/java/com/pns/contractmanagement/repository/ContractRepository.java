package com.pns.contractmanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pns.contractmanagement.model.Contract;


public interface ContractRepository extends MongoRepository<Contract, Long> {
    
    
}
