package com.pns.contractmanagement.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerEntity extends AbstractMongoEntity {

    private String name;

    private String region;

}
