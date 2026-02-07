package com.payment.stripe_provider_service.service;

import com.payment.stripe_provider_service.dto.StripeCreatePaymentRequest;
import com.payment.stripe_provider_service.dto.StripePaymentResponse;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    @Value("${stripe.webhook.secret}")
    private String stripeSecretKey;

    public StripePaymentResponse create(StripeCreatePaymentRequest req) {

        try {
            Stripe.apiKey = stripeSecretKey;

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)

                            // ⭐ SOURCE OF TRUTH
                            .setClientReferenceId(req.getPaymentId())

                            .setSuccessUrl("http://localhost:3000/success")
                            .setCancelUrl("http://localhost:3000/cancel")

                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(req.getCurrency())
                                                            .setUnitAmount(req.getAmount())
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Order Payment")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            Session session = Session.create(params);

            return new StripePaymentResponse(session.getUrl());

        } catch (Exception e) {
            throw new RuntimeException("Stripe checkout creation failed", e);
        }
    }
}

