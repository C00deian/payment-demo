package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.provider.PaymentProvider;
import com.payment.payment_integration_service.provider.StripeProviderClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderRouter {

	private final Map<String, PaymentProvider> providers;

	public ProviderRouter(StripeProviderClient stripeProviderClient) {
		providers = new HashMap<>();
		providers.put("STRIPE", stripeProviderClient);
	}

	public PaymentProvider route(String provider) {
		return providers.get(provider);
	}
}
