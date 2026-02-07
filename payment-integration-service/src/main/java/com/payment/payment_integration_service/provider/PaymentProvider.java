package com.payment.payment_integration_service.provider;

import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.model.Payment;

public interface PaymentProvider {

    PaymentResponse createPayment(Payment payment);
    String getProviderName();
}
