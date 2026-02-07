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

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public StripePaymentResponse create(StripeCreatePaymentRequest req) {

        try {
            if (stripeSecretKey == null || stripeSecretKey.trim().isEmpty()) {
                throw new IllegalStateException("Missing Stripe secret key (set stripe.secret.key / STRIPE_SECRET_KEY)");
            }
            Stripe.apiKey = stripeSecretKey;

            String successUrl = firstNonBlank(req.getSuccessUrl(), "http://localhost:3000/success");
            String cancelUrl = firstNonBlank(req.getFailureUrl(), "http://localhost:3000/cancel");

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)

                            // ⭐ SOURCE OF TRUTH
                            .setClientReferenceId(req.getPaymentId())

                            .putMetadata("paymentId", req.getPaymentId())
                            .setPaymentIntentData(
                                    SessionCreateParams.PaymentIntentData.builder()
                                            .putMetadata("paymentId", req.getPaymentId())
                                            .build()
                            )

                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)

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

            StripePaymentResponse response = new StripePaymentResponse(session.getUrl());
            response.setStripeSessionId(session.getId());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Stripe checkout creation failed", e);
        }
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value == null) continue;
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) return trimmed;
        }
        return null;
    }
}
