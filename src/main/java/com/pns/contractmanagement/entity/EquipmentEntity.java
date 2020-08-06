package com.pns.contractmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public  class EquipmentEntity  extends AbstractMongoEntity{
    
    private String model;
    
    private String description;
    
}
