package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.exception.UnsupportedProviderException;
import com.payment.payment_integration_service.provider.PaymentProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProviderRouter {

	private final Map<String, PaymentProvider> providers;

	public ProviderRouter(List<PaymentProvider> providerList) {
		Map<String, PaymentProvider> map = new HashMap<>();
		for (PaymentProvider provider : providerList) {
			map.put(provider.getProviderName(), provider);
		}
		this.providers = Map.copyOf(map);
	}

	public PaymentProvider route(String provider) {
		PaymentProvider paymentProvider = providers.get(provider);
		if (paymentProvider == null) {
			throw new UnsupportedProviderException(provider);
		}
		return paymentProvider;
	}
}
