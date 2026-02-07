package com.payment.stripe_provider_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class StripeProviderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StripeProviderServiceApplication.class, args);
	}

}
