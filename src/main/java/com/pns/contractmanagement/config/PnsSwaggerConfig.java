package com.pns.contractmanagement.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class PnsSwaggerConfig {

	@Bean
	public Docket swaggerConfig() {
		List<? extends SecurityScheme> securitySchemes = List.of(new ApiKey("Bearer ","Authorization","header"));
		return new Docket(DocumentationType.SWAGGER_2).securitySchemes(securitySchemes).select()
				.apis(RequestHandlerSelectors.basePackage("com.pns.contractmanagement.controller")).build();
	}
}
