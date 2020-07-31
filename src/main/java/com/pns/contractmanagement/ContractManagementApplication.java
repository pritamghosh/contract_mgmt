package com.pns.contractmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContractManagementApplication {
	private Logger LOGGER = LoggerFactory.getLogger(ContractManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ContractManagementApplication.class, args);
	}

}
