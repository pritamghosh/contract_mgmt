package com.pns.contractmanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pns.contractmanagement.model.Customer;
import com.pns.contractmanagement.model.Equipment;


public interface EquipmentRepository extends MongoRepository<Equipment, Long> {
}
