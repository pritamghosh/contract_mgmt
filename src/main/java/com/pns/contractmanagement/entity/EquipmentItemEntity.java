package com.pns.contractmanagement.entity;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class EquipmentItemEntity extends BaseMongoEntity {

    @JsonProperty("equipment_id")
    @BsonIgnore
    private String equipmentId;

    @BsonProperty("equipment_id")
    @JsonIgnore
    private ObjectId equipmentObjectId;

    private String serialNumber;

    public void setEquipmentId(final String equipmentDetailId) {
        this.equipmentId = equipmentDetailId;
        equipmentObjectId = new ObjectId(equipmentDetailId);
    }

    public void setEquipmentObjectId(final ObjectId equipmentDetailObjectId) {
        this.equipmentObjectId = equipmentDetailObjectId;
        equipmentId = equipmentDetailObjectId.toHexString();
    }
}
