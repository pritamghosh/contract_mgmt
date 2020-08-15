package com.pns.contractmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.service.impl.EquipmentServiceImpl;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

	@Autowired
	EquipmentServiceImpl service;

	@PutMapping
	public Equipment addEquipment(@RequestBody final Equipment equipment) throws PnsException {
		return service.addEquipment(equipment);
	}

	@PostMapping
	public Equipment modifyEquipment(@RequestBody final Equipment equipment) throws PnsException {
		return service.modifyEquipment(equipment);
	}

	@GetMapping(params = { "!model" })
	public List<Equipment> getAllEquipments() throws PnsException {
		return service.getAllEquipments();
	}

	@GetMapping(params = { "model" })
	public List<Equipment> getEquipmentsByModel(@RequestParam("model") final String model) throws PnsException {
		return service.getEquipmentsByModel(model);
	}

	@PutMapping("/item")
	public EquipmentItem addEquipmentItem(@RequestBody final EquipmentItem equipment) throws PnsException {
		return service.addEquipmentItem(equipment);
	}

	@PostMapping("/item")
	public EquipmentItem modifyEquipmentItem(@RequestBody final EquipmentItem equipment) throws PnsException {
		return service.modifyEquipmentItem(equipment);
	}

	@DeleteMapping("/item/{id}")
	public EquipmentItem DeleteEquipmentById(@PathVariable("id") final String id) throws PnsException {
		return service.DeleteEquipmentById(id);
	}

	@GetMapping(value = "/count/{id}")
	public ResponseEntity<Long> getCountById(@PathVariable("id") final String id) {
		return ResponseEntity.ok(service.getEquipmentCountById(id));
	}

	@GetMapping("/item/{id}")
	public EquipmentItem getEquipmentbyid(@PathVariable("id") final String id) throws PnsException {
		return service.getEquipmentItemById(id);
	}

	@GetMapping("/item")
	public List<EquipmentItem> getAllEquipmentItem() {
		return service.getAllEquipmentItem();
	}

	@GetMapping(value = "/search", params = { "query" })
	public List<Equipment> searchEquipmentbyQuery(@RequestParam("query") final String query) throws PnsException {
		return service.searchEquipmentbyQuery(query);
	}
}
