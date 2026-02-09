package com.projexflow.serviceregistry.ProjexFlow_SR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ProjexFlowSrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjexFlowSrApplication.class, args);
	}

}
