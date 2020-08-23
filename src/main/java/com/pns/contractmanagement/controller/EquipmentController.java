package com.pns.contractmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.SearchResponse;
import com.pns.contractmanagement.service.impl.EquipmentServiceImpl;

@RestController
@RequestMapping("/equipment")
@RestControllerAdvice
public class EquipmentController {

	@Autowired
	private EquipmentServiceImpl service;

	@PutMapping
	@PreAuthorize("hasRole('create')")
	public Equipment addEquipment(@RequestBody final Equipment equipment) {
		return service.addEquipment(equipment);
	}

	@PostMapping
	@PreAuthorize("hasRole('update')")
	public Equipment modifyEquipment(@RequestBody final Equipment equipment) {
		return service.modifyEquipment(equipment);
	}

	@GetMapping(params = { "!model" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Equipment> getAllEquipments(
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getAllEquipments(page);
	}

	@GetMapping(params = { "model" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Equipment> getEquipmentsByModel(@RequestParam("model") final String model,
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.getEquipmentsByModel(model, page);
	}

	@PutMapping("/item")
	@PreAuthorize("hasRole('create')")
	public EquipmentItem addEquipmentItem(@RequestBody final EquipmentItem equipment) {
		return service.addEquipmentItem(equipment);
	}

	@PostMapping("/item")
	@PreAuthorize("hasRole('update')")
	public EquipmentItem modifyEquipmentItem(@RequestBody final EquipmentItem equipment) {
		return service.modifyEquipmentItem(equipment);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('delete')")
	public Equipment deleteEquipmentById(@PathVariable("id") final String id) {
		return service.deleteEquipmentById(id);
	}

	@GetMapping(value = "/count/{id}")
	@PreAuthorize("hasRole('read')")
	public ResponseEntity<Long> getCountById(@PathVariable("id") final String id) {
		return ResponseEntity.ok(service.getEquipmentCountById(id));
	}

	@GetMapping("/item/{id}")
	@PreAuthorize("hasRole('read')")
	public EquipmentItem getEquipmentbyid(@PathVariable("id") final String id) {
		return service.getEquipmentItemById(id);
	}

	@GetMapping("/item")
	@PreAuthorize("hasRole('read')")
	public List<EquipmentItem> getAllEquipmentItem() {
		return service.getAllEquipmentItem();
	}

	@GetMapping(value = "/search", params = { "query" })
	@PreAuthorize("hasRole('read')")
	public SearchResponse<Equipment> searchEquipmentbyQuery(@RequestParam("query") final String query,
			@RequestParam(value = "page", defaultValue = "1") final int page) {
		return service.searchEquipmentbyQuery(query, page);
	}
}
