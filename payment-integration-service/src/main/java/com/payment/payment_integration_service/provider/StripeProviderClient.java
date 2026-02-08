package com.payment.payment_integration_service.provider;

import com.payment.payment_integration_service.client.stripe.StripePaymentClientResponse;
import com.payment.payment_integration_service.client.stripe.StripeProviderFeignClient;
import com.payment.payment_integration_service.mapper.StripeClientMapper;
import com.payment.payment_integration_service.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StripeProviderClient implements PaymentProvider {

    private final StripeProviderFeignClient stripeClient;
    private final StripeClientMapper mapper;


    @Override
    public String getProviderName() {
        return "STRIPE";
    }


    @Override
    public PaymentInitiationResult initiatePayment(Payment payment) {
        StripePaymentClientResponse resp =
                stripeClient.createPayment(mapper.toCreateRequest(payment));

        return mapper.toInitiationResult(resp);
    }
}
