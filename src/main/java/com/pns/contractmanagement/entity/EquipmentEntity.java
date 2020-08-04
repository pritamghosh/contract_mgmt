package com.pns.contractmanagement.entity;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public  class EquipmentEntity  extends AbstractMongoEntity{
    
    private String model;
    
    private String description;
    
}
