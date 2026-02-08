package com.payment.stripe_provider_service.service;

import com.payment.stripe_provider_service.dto.StripeWebhookUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-integration-service",
        url = "${payment.integration.base-url}"
)
public interface PaymentIntegrationFeignClient {

    @PostMapping("/internal/payments/{paymentId}/success")
    void markSuccess(@PathVariable("paymentId") String paymentId);

    @PostMapping("/internal/payments/{paymentId}/failed")
    void markFailed(@PathVariable("paymentId") String paymentId);

    @PostMapping("/internal/webhooks/stripe")
    void stripeWebhook(@RequestBody StripeWebhookUpdateRequest request);
}
