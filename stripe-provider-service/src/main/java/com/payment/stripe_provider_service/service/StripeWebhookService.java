package com.payment.stripe_provider_service.service;

import com.payment.stripe_provider_service.dto.WebhookUpdateRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeWebhookService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PaymentIntegrationFeignClient client;

    public StripeWebhookService(PaymentIntegrationFeignClient client) {
        this.client = client;
    }

    public void process(String payload, String signature)
            throws SignatureVerificationException {

        Event event = Webhook.constructEvent(
                payload, signature, webhookSecret
        );

        String type = event.getType();

        if (!type.startsWith("payment_intent")) {
            return;
        }

        PaymentIntent intent = (PaymentIntent)
                event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

        if (intent == null) return;

        String paymentId =
                intent.getMetadata().get("paymentId");

        if (paymentId == null) return;

        if ("payment_intent.succeeded".equals(type)) {
            client.markSuccess(new WebhookUpdateRequest(paymentId));
        }

        if ("payment_intent.payment_failed".equals(type)) {
            client.markFailed(new WebhookUpdateRequest(paymentId));
        }
    }
}

