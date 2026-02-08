package com.payment.payment_integration_service.provider;

import lombok.Data;

@Data
public class PaymentInitiationResult {
    private String redirectUrl;
    private String providerSessionId;
}

