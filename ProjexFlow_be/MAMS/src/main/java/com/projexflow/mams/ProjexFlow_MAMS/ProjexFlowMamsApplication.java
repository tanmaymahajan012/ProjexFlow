package com.projexflow.mams.ProjexFlow_MAMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProjexFlowMamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjexFlowMamsApplication.class, args);
	}

}
