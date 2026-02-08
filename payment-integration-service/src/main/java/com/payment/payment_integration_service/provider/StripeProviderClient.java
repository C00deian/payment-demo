package com.payment.payment_integration_service.provider;

import com.payment.payment_integration_service.client.stripe.StripeCreatePaymentClientRequest;
import com.payment.payment_integration_service.client.stripe.StripePaymentClientResponse;
import com.payment.payment_integration_service.client.stripe.StripeProviderFeignClient;
import com.payment.payment_integration_service.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class StripeProviderClient implements PaymentProvider {

    private final StripeProviderFeignClient stripeClient;

    public StripeProviderClient(StripeProviderFeignClient stripeClient) {
        this.stripeClient = stripeClient;
    }


    @Override
    public String getProviderName() {
        return "STRIPE";
    }


    @Override
    public PaymentInitiationResult initiatePayment(Payment payment) {

        StripeCreatePaymentClientRequest req =
                new StripeCreatePaymentClientRequest();

        req.setPaymentId(payment.getPaymentId());
        req.setAmount(payment.getAmount());
        req.setCurrency(payment.getCurrency());

        StripePaymentClientResponse resp =
                stripeClient.createPayment(req);

        PaymentInitiationResult result = new PaymentInitiationResult();
        result.setRedirectUrl(resp.getCheckoutUrl());
        result.setProviderSessionId(resp.getStripeSessionId());
        return result;
    }
}
