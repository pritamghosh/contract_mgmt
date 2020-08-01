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
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.service.impl.EquipmentServiceImpl;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final Logger LOGGER = LoggerFactory.getLogger(EquipmentController.class);

    @Autowired
    EquipmentServiceImpl service;

    @PutMapping
    public Equipment addEquipment(final Equipment equipment) throws PnsException {
        return service.addEquipment(equipment);
    }

    @PostMapping
    public Equipment modifyEquipment(final Equipment equipment) throws PnsException {
        return service.modifyEquipment(equipment);
    }

    @DeleteMapping("/{id}")
    public Equipment DeleteEquipmentById(@PathVariable("id") final long id) throws PnsException {
        return service.DeleteEquipmentById(id);
    }

    @GetMapping("{id}")
    public Equipment getEquipmentbyid(@PathVariable("id") final long id) throws PnsException {
        return service.getEquipmentbyid(id);
    }

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return service.getAllEquipment();
    }
}
