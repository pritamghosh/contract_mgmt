package com.pns.contractmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableMongoRepositories
public class ContractManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractManagementApplication.class, args);
    }
    

}
