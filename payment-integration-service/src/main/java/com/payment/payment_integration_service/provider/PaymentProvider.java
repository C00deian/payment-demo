package com.payment.payment_integration_service.provider;

import com.payment.payment_integration_service.model.Payment;

public interface PaymentProvider {

    PaymentInitiationResult initiatePayment(Payment payment);
    String getProviderName();
}
