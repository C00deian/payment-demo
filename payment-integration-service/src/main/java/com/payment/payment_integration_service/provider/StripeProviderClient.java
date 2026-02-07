package com.payment.payment_integration_service.provider;

import com.payment.payment_integration_service.client.stripe.StripeCreatePaymentClientRequest;
import com.payment.payment_integration_service.client.stripe.StripePaymentClientResponse;
import com.payment.payment_integration_service.client.stripe.StripeProviderFeignClient;
import com.payment.payment_integration_service.dto.PaymentResponse;
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
    public PaymentResponse createPayment(Payment payment) {

        StripeCreatePaymentClientRequest req =
                new StripeCreatePaymentClientRequest();

        req.setPaymentId(payment.getPaymentId());
        req.setAmount(payment.getAmount());
        req.setCurrency(payment.getCurrency());

        StripePaymentClientResponse resp =
                stripeClient.createPayment(req);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setRedirectUrl(resp.getCheckoutUrl());
        response.setStatus("CREATED");

        return response;
    }
}
