package com.payment.stripe_provider_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.stripe_provider_service.dto.StripeWebhookUpdateRequest;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StripeWebhookService {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PaymentIntegrationFeignClient client;
    private final ObjectMapper objectMapper;

    public StripeWebhookService(PaymentIntegrationFeignClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void process(String payload, String signature)
            throws SignatureVerificationException {

        Event event = Webhook.constructEvent(
                payload, signature, webhookSecret
        );

        String type = event.getType();
        String eventId = event.getId();

        StripeWebhookUpdateRequest update = buildUpdateRequest(payload, eventId, type);
        if (update == null) {
            return;
        }

        try {
            client.stripeWebhook(update);
        } catch (FeignException.NotFound e) {
            log.warn("Ignoring Stripe webhook {} for unknown paymentId={}", type, update.getPaymentId());
        }
    }

    private StripeWebhookUpdateRequest buildUpdateRequest(String payload, String eventId, String type) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode object = root.path("data").path("object");

            if ("checkout.session.completed".equals(type)) {
                String paymentStatus = textOrNull(object.path("payment_status"));
                if (paymentStatus != null && !"paid".equals(paymentStatus)) {
                    log.info("Ignoring checkout.session.completed with payment_status={}", paymentStatus);
                    return null;
                }

                String paymentId = firstNonBlank(
                        textOrNull(object.path("client_reference_id")),
                        textOrNull(object.path("metadata").path("paymentId"))
                );
                if (paymentId == null) {
                    log.warn("Stripe webhook {} missing paymentId (eventId={})", type, eventId);
                    return null;
                }

                StripeWebhookUpdateRequest req = new StripeWebhookUpdateRequest();
                req.setPaymentId(paymentId);
                req.setStripeEventId(eventId);
                req.setStripeEventType(type);
                req.setStripeObjectId(textOrNull(object.path("id")));
                req.setStripeSessionId(textOrNull(object.path("id")));
                req.setStripePaymentIntentId(textOrNull(object.path("payment_intent")));
                req.setStatus("SUCCESS");
                return req;
            }

            if ("payment_intent.succeeded".equals(type) || "payment_intent.payment_failed".equals(type)) {
                String paymentId = textOrNull(object.path("metadata").path("paymentId"));
                if (paymentId == null) {
                    log.warn("Stripe webhook {} missing metadata.paymentId (eventId={})", type, eventId);
                    return null;
                }

                String status = "payment_intent.succeeded".equals(type) ? "SUCCESS" : "FAILED";

                StripeWebhookUpdateRequest req = new StripeWebhookUpdateRequest();
                req.setPaymentId(paymentId);
                req.setStripeEventId(eventId);
                req.setStripeEventType(type);
                req.setStripeObjectId(textOrNull(object.path("id")));
                req.setStripePaymentIntentId(textOrNull(object.path("id")));
                req.setStatus(status);
                return req;
            }

            return null;
        } catch (IOException e) {
            log.warn("Failed to parse Stripe webhook JSON for type={}", type, e);
            return null;
        }
    }

    private static String textOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String value = node.asText(null);
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
