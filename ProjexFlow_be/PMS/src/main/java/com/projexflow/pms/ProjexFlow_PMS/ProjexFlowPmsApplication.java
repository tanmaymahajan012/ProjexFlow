package com.projexflow.pms.ProjexFlow_PMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProjexFlowPmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjexFlowPmsApplication.class, args);
	}

}
