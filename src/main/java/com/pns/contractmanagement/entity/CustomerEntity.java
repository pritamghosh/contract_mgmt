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
public class CustomerEntity extends BaseMongoEntity {

    private String name;

    private String region;
    
    private String address;
    
    private String gstinNo;
    
    private String pan;

}
