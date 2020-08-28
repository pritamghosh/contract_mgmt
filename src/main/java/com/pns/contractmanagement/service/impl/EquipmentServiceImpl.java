package com.pns.contractmanagement.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.impl.EquipmentDaoImpl;
import com.pns.contractmanagement.dao.impl.EquipmentItemDaoImpl;
import com.pns.contractmanagement.entity.EquipmentEntity;
import com.pns.contractmanagement.entity.EquipmentItemEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableEquipment;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;
import com.pns.contractmanagement.model.ImmutableSearchResponse;
import com.pns.contractmanagement.model.SearchResponse;

@Service
public class EquipmentServiceImpl {

	@Autowired
	EquipmentDaoImpl equipmentDaoImpl;

	@Autowired
	EquipmentItemDaoImpl equipmentItemDaoImpl;

	public EquipmentItem addEquipmentItem(final EquipmentItem equipmentItem) {
		final EquipmentItemEntity insertedEquipment = equipmentItemDaoImpl.insert(map(equipmentItem));
		return map(insertedEquipment, getEquipmentById(insertedEquipment.getEquipmentId()));
	}

	public EquipmentItem modifyEquipmentItem(final EquipmentItem equipmentItem) {
		equipmentItemDaoImpl.update(map(equipmentItem));
		return getEquipmentItemById(equipmentItem.getId());
	}

	public Equipment deleteEquipmentById(final String id) {
		final Equipment deletedEquipment = getEquipmentById(id);
		equipmentDaoImpl.deleteById(id);
		return deletedEquipment;
	}

	public EquipmentItem getEquipmentItemById(final String id) {
		final EquipmentItemEntity entity = equipmentItemDaoImpl.findById(id)
				.orElseThrow(() -> new PnsException("Equipment Not Found!!", PnsError.NOT_FOUND));
		return map(entity, getEquipmentById(entity.getEquipmentId()));
	}

	public SearchResponse<Equipment> searchEquipmentbyQuery(final String query, final int page) {
		return ImmutableSearchResponse.<Equipment>builder()
				.result(mapEquipment(equipmentDaoImpl.searchByQuery(query, page)))
				.pageCount(equipmentDaoImpl.countDocumnetsByQuery(query)).build();
	}

	public List<EquipmentItem> getAllEquipmentItem() {
		return map(equipmentItemDaoImpl.findAll());
	}

	public Equipment modifyEquipment(final Equipment equipment) {
		equipmentDaoImpl.update(map(equipment));
		return getEquipmentById(equipment.getId());
	}

	public Equipment addEquipment(final Equipment equipments) {
		return map(equipmentDaoImpl.insert(map(equipments)));
	}

	public Equipment getEquipmentById(final String id) {
		return map(equipmentDaoImpl.findById(id)
				.orElseThrow(() -> new PnsException("Equipment Detail Not Found!!", PnsError.NOT_FOUND)));
	}

	public SearchResponse<Equipment> getAllEquipments(final int page) {
		return ImmutableSearchResponse.<Equipment>builder()
				.result(equipmentDaoImpl.findAll(page).stream().map(this::map).collect(Collectors.toList()))
				.pageCount(equipmentDaoImpl.countAllDocumnets()).build();
	}

	public long getEquipmentCountById(final String id) {
		return equipmentItemDaoImpl.getCountById(id);
	}

	public SearchResponse<Equipment> getEquipmentsByModel(final String model, final int page) {
		final List<Equipment> collect = equipmentDaoImpl.findByModel(model, page).stream().map(this::map)
				.collect(Collectors.toList());
		return ImmutableSearchResponse.<Equipment>builder().result(collect)
				.pageCount(equipmentDaoImpl.countDocumnetsByModel(model)).build();
	}

	private EquipmentItemEntity map(final EquipmentItem equipment) {
		final EquipmentItemEntity entity = new EquipmentItemEntity();
		entity.setEquipmentId(equipment.getEquipment().getId());
		entity.setSerialNumber(equipment.getSerialNumber());
		entity.setId(equipment.getId());
		return entity;
	}

	private EquipmentItem map(final EquipmentItemEntity entity, final Equipment equipment) {
		return ImmutableEquipmentItem.builder().id(entity.getId()).serialNumber(entity.getSerialNumber())
				.equipment(equipment).build();
	}

	private List<EquipmentItem> map(final Collection<EquipmentItemEntity> list,
			final Map<String, EquipmentEntity> map) {
		return list.stream().map(e -> map(e, map(map.get(e.getEquipmentId())))).collect(Collectors.toList());
	}

	private Equipment map(final EquipmentEntity entity) {
		return ImmutableEquipment.builder().id(entity.getId()).description(entity.getDescription())
				.model(entity.getModel()).build();
	}

	private EquipmentEntity map(final Equipment equipments) {
		final EquipmentEntity entity = new EquipmentEntity();
		entity.setId(equipments.getId());
		entity.setDescription(equipments.getDescription());
		entity.setModel(equipments.getModel());
		return entity;
	}

	private List<Equipment> mapEquipment(final List<EquipmentEntity> searchByQuery) {
		return searchByQuery.stream().map(this::map).collect(Collectors.toList());
	}

	private List<EquipmentItem> map(final List<EquipmentItemEntity> list) {
		return map(list, equipmentDaoImpl
				.findByIds(list.stream().map(EquipmentItemEntity::getEquipmentId).collect(Collectors.toList())));
	}

}
