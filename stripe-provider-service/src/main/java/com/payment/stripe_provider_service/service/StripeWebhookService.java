package com.payment.stripe_provider_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

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

        Object stripeObject = event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if ("checkout.session.completed".equals(type)) {
            if (!(stripeObject instanceof Session session)) {
                String paymentId = extractPaymentIdFromPayload(payload, type);
                if (paymentId == null) {
                    log.warn("Stripe webhook received {} (eventId={}) but could not deserialize/extract checkout session", type, eventId);
                    return;
                }
                markSuccess(paymentId, type);
                return;
            }
            handleCheckoutSessionCompleted(session, type);
            return;
        }

        if ("payment_intent.succeeded".equals(type) || "payment_intent.payment_failed".equals(type)) {
            if (!(stripeObject instanceof PaymentIntent intent)) {
                String paymentId = extractPaymentIdFromPayload(payload, type);
                if (paymentId == null) {
                    log.warn("Stripe webhook received {} (eventId={}) but could not deserialize/extract payment intent", type, eventId);
                    return;
                }
                if ("payment_intent.succeeded".equals(type)) {
                    markSuccess(paymentId, type);
                } else {
                    markFailed(paymentId, type);
                }
                return;
            }
            handlePaymentIntent(intent, type);
        }
    }

    private void handleCheckoutSessionCompleted(Session session, String type) {
        if (session == null) {
            log.warn("Stripe webhook received {} but session was null", type);
            return;
        }

        String paymentStatus = session.getPaymentStatus();
        if (paymentStatus != null && !"paid".equals(paymentStatus)) {
            log.warn("Stripe webhook received {} but session paymentStatus={} (sessionId={})", type, paymentStatus, session.getId());
            return;
        }

        String paymentId = firstNonBlank(
                session.getClientReferenceId(),
                getMetadataValue(session.getMetadata(), "paymentId")
        );

        if (paymentId == null) {
            log.warn("Stripe webhook received {} but no paymentId/clientReferenceId found (sessionId={})", type, session.getId());
            return;
        }

        markSuccess(paymentId, type);
    }

    private void handlePaymentIntent(PaymentIntent intent, String type) {
        if (intent == null) {
            log.warn("Stripe webhook received {} but payment intent was null", type);
            return;
        }

        String paymentId = getMetadataValue(intent.getMetadata(), "paymentId");
        if (paymentId == null) {
            log.warn("Stripe webhook received {} but metadata.paymentId missing (paymentIntentId={})", type, intent.getId());
            return;
        }

        if ("payment_intent.succeeded".equals(type)) {
            markSuccess(paymentId, type);
        } else if ("payment_intent.payment_failed".equals(type)) {
            markFailed(paymentId, type);
        }
    }

    private String extractPaymentIdFromPayload(String payload, String type) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode object = root.path("data").path("object");

            if ("checkout.session.completed".equals(type)) {
                String paymentStatus = textOrNull(object.path("payment_status"));
                if (paymentStatus != null && !"paid".equals(paymentStatus)) {
                    return null;
                }
                return firstNonBlank(
                        textOrNull(object.path("client_reference_id")),
                        textOrNull(object.path("metadata").path("paymentId"))
                );
            }

            if ("payment_intent.succeeded".equals(type) || "payment_intent.payment_failed".equals(type)) {
                return textOrNull(object.path("metadata").path("paymentId"));
            }

            return null;
        } catch (IOException e) {
            log.warn("Failed to parse Stripe webhook JSON for type={}", type, e);
            return null;
        }
    }

    private void markSuccess(String paymentId, String eventType) {
        try {
            client.markSuccess(paymentId);
        } catch (FeignException.NotFound e) {
            log.warn("Ignoring Stripe webhook {} for unknown paymentId={}", eventType, paymentId);
        }
    }

    private void markFailed(String paymentId, String eventType) {
        try {
            client.markFailed(paymentId);
        } catch (FeignException.NotFound e) {
            log.warn("Ignoring Stripe webhook {} for unknown paymentId={}", eventType, paymentId);
        }
    }

    private static String getMetadataValue(Map<String, String> metadata, String key) {
        if (metadata == null) return null;
        String value = metadata.get(key);
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
