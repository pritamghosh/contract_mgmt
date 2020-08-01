package com.pns.contractmanagement.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.repository.EquipmentRepository;

@Service
public class EquipmentServiceImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    @Autowired
    EquipmentRepository repository;

    public Equipment addEquipment(final Equipment equipment) throws PnsException {
        if (!repository.findById(equipment.getId()).isPresent()) {
            return repository.save(equipment);
        }
        throw new PnsException("Equipment is already present with same details", PnsError.DUPLICTE_RECORD);
    }

    public Equipment modifyEquipment(final Equipment equipment) throws PnsException {
        getEquipmentbyid(equipment.getId());
        return repository.save(equipment);
    }

    public Equipment DeleteEquipmentById(final long id) throws PnsException {
        final Equipment deletedEquipment = getEquipmentbyid(id);
        repository.deleteById(id);
        return deletedEquipment;
    }

    public Equipment getEquipmentbyid(final long id) throws PnsException {
        return repository.findById(id).orElseThrow(() -> new PnsException("Equipment Not Found!!", PnsError.NOT_FOUND));
    }

    public List<Equipment> getAllEquipment() {
        return repository.findAll();
    }
}
