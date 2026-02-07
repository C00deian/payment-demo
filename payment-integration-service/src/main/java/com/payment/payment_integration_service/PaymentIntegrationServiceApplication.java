package com.payment.payment_integration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PaymentIntegrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentIntegrationServiceApplication.class, args);
	}

}

