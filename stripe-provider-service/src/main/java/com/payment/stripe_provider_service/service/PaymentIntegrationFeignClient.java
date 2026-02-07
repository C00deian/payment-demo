package com.payment.stripe_provider_service.service;

import com.payment.stripe_provider_service.dto.WebhookUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-integration-service",
        url = "${payment.integration.base-url}"
)
public interface PaymentIntegrationFeignClient {

    @PostMapping("/internal/payments/success")
    void markSuccess(@RequestBody WebhookUpdateRequest request);

    @PostMapping("/internal/payments/failed")
    void markFailed(@RequestBody WebhookUpdateRequest request);
}
