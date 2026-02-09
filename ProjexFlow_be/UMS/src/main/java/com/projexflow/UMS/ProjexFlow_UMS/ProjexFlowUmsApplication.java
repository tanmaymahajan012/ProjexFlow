package com.projexflow.UMS.ProjexFlow_UMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.projexflow.UMS.ProjexFlow_UMS")
@EnableJpaRepositories(basePackages = "com.projexflow.UMS.ProjexFlow_UMS")
@EnableDiscoveryClient
public class ProjexFlowUmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjexFlowUmsApplication.class, args);
	}

}
