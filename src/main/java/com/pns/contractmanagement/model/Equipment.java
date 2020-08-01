package com.pns.contractmanagement.model;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(builder = Equipment.EquipmentBuilder.class)
public class Equipment {
    @Id
    private long id;
    
    private String name;
}
