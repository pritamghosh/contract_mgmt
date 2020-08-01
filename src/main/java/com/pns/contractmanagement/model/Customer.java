package com.pns.contractmanagement.model;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonDeserialize(builder = Customer.CustomerBuilder.class)
@AllArgsConstructor@NoArgsConstructor
public class Customer {

    @Id
    private long id;

    private String name;
    
    private String region;
}
