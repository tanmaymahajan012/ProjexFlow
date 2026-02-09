package com.projexflow.apigateway.ProjexFlow_APIGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProjexFlowApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjexFlowApiGatewayApplication.class, args);
	}

}
