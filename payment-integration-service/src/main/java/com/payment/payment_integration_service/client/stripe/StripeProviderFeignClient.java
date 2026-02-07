package com.payment.payment_integration_service.client.stripe;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "stripe-provider",
        url = "${provider.stripe.base-url}"
)
public interface StripeProviderFeignClient {

    @PostMapping("/stripe/payments")
    StripePaymentClientResponse createPayment(
            @RequestBody StripeCreatePaymentClientRequest request
    );
}
