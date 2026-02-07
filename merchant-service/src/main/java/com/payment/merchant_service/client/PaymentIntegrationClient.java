package com.payment.merchant_service.client;

import com.payment.merchant_service.dto.PaymentRequest;
import com.payment.merchant_service.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-integration-service",
        url = "${payment.integration.base-url}"
)
public interface PaymentIntegrationClient {

    @PostMapping("/payments")
    PaymentResponse createPayment(@RequestBody PaymentRequest request);
}
