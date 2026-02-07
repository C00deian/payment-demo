package com.payment.stripe_provider_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "payment-integration-service",
        url = "${payment.integration.base-url}"
)
public interface PaymentIntegrationFeignClient {

    @PostMapping("/internal/payments/{paymentId}/success")
    void markSuccess(@PathVariable("paymentId") String paymentId);

    @PostMapping("/internal/payments/{paymentId}/failed")
    void markFailed(@PathVariable("paymentId") String paymentId);
}
