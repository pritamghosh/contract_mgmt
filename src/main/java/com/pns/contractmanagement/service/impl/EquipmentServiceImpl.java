package com.pns.contractmanagement.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pns.contractmanagement.dao.EquipmentDao;
import com.pns.contractmanagement.dao.EquipmentItemDao;
import com.pns.contractmanagement.entity.EquipmentEntity;
import com.pns.contractmanagement.entity.EquipmentItemEntity;
import com.pns.contractmanagement.exceptions.PnsError;
import com.pns.contractmanagement.exceptions.PnsException;
import com.pns.contractmanagement.model.Equipment;
import com.pns.contractmanagement.model.EquipmentItem;
import com.pns.contractmanagement.model.ImmutableEquipment;
import com.pns.contractmanagement.model.ImmutableEquipmentItem;

@Service
public class EquipmentServiceImpl {

    @Autowired
    EquipmentDao equipmentDao;

    @Autowired
    EquipmentItemDao equipmentItemDao;

    public EquipmentItem addEquipmentItem(final EquipmentItem equipmentItem) throws PnsException {
        final EquipmentItemEntity insertedEquipment = equipmentItemDao.insert(map(equipmentItem));
        return map(insertedEquipment, getEquipmentById(insertedEquipment.getEquipmentId()));
    }

    public EquipmentItem modifyEquipmentItem(final EquipmentItem equipmentItem) throws PnsException {
        equipmentItemDao.update(map(equipmentItem));
        return getEquipmentItemById(equipmentItem.getId());
    }

    public EquipmentItem DeleteEquipmentById(final String id) throws PnsException {
        final EquipmentItem deletedEquipment = getEquipmentItemById(id);
        equipmentItemDao.deleteById(id);
        return deletedEquipment;
    }

    public EquipmentItem getEquipmentItemById(final String id) throws PnsException {
        final EquipmentItemEntity entity = equipmentItemDao.findById(id)
            .orElseThrow(() -> new PnsException("Equipment Not Found!!", PnsError.NOT_FOUND));
        return map(entity, getEquipmentById(entity.getEquipmentId()));
    }

    public List<Equipment> searchEquipmentbyQuery(final String query) {
        return mapEquipment(equipmentDao.searchByQuery(query));
    }

    public List<EquipmentItem> getAllEquipmentItem() {
        return map(equipmentItemDao.findAll());
    }


    public Equipment modifyEquipment(final Equipment equipment) throws PnsException {
        equipmentDao.update(map(equipment));
        return getEquipmentById(equipment.getId());
    }

    public Equipment addEquipment(final Equipment equipments) {
        return map(equipmentDao.insert(map(equipments)));
    }

    public Equipment getEquipmentById(final String id) throws PnsException {
        return map(equipmentDao.findById(id)
            .orElseThrow(() -> new PnsException("Equipment Detail Not Found!!", PnsError.NOT_FOUND)));
    }
    public List<Equipment> getAllEquipments() {
        return equipmentDao.findAll().stream().map(e->map(e)).collect(Collectors.toList());
    }
    
    public long getEquipmentCountById(String id) {
        return equipmentItemDao.getCountById(id);
    }
    
    public List<Equipment> getEquipmentsByModel(final String model) {
    	return equipmentDao.findByModel(model).stream().map(e->map(e)).collect(Collectors.toList());
	}
    
    public long getEquipmentCountByIds(List<String> ids) {
        return 0;
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

    private List<EquipmentItem> map(final Collection<EquipmentItemEntity> list, final Map<String, EquipmentEntity> map) {
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
        return searchByQuery.stream().map(e->map(e)).collect(Collectors.toList());
    }
    private List<EquipmentItem> map(final List<EquipmentItemEntity> list) {
        return map(list, equipmentDao
            .findByIds(list.stream().map(e -> e.getEquipmentId()).collect(Collectors.toList())));
    }





}