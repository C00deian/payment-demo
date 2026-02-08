package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.StripeWebhookUpdateRequest;
import com.payment.payment_integration_service.exception.PaymentNotFoundException;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.model.PaymentStatus;
import com.payment.payment_integration_service.model.PaymentWebhookEvent;
import com.payment.payment_integration_service.model.WebhookStatusUpdate;
import com.payment.payment_integration_service.repository.PaymentRepository;
import com.payment.payment_integration_service.repository.PaymentWebhookEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeWebhookUpdateService {

    private static final String PROVIDER = "STRIPE";

    private final PaymentWebhookEventRepository eventRepository;
    private final PaymentRepository paymentRepository;

    public StripeWebhookUpdateService(PaymentWebhookEventRepository eventRepository, PaymentRepository paymentRepository) {
        this.eventRepository = eventRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void apply(StripeWebhookUpdateRequest request) {
        String paymentId = trimToNull(request.getPaymentId());
        String eventId = trimToNull(request.getStripeEventId());
        String eventType = trimToNull(request.getStripeEventType());
        String status = trimToNull(request.getStatus());

        if (paymentId == null || eventId == null || eventType == null || status == null) {
            throw new IllegalArgumentException("Missing required webhook fields");
        }

        if (eventRepository.existsByProviderAndProviderEventId(PROVIDER, eventId)) {
            return;
        }

        WebhookStatusUpdate update = WebhookStatusUpdate.valueOf(status);

        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.setProvider(PROVIDER);
        event.setProviderEventId(eventId);
        event.setEventType(eventType);
        event.setObjectId(trimToNull(request.getStripeObjectId()));
        event.setPaymentId(paymentId);
        event.setStatusUpdate(update);

        try {
            eventRepository.save(event);
        } catch (DataIntegrityViolationException ignored) {
            return;
        }

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!PROVIDER.equals(payment.getProvider())) {
            return;
        }

        // store provider references for audit/reconciliation
        String sessionId = trimToNull(request.getStripeSessionId());
        if (sessionId != null && payment.getProviderSessionId() == null) {
            payment.setProviderSessionId(sessionId);
        }

        String paymentIntentId = trimToNull(request.getStripePaymentIntentId());
        if (paymentIntentId != null && payment.getProviderPaymentIntentId() == null) {
            payment.setProviderPaymentIntentId(paymentIntentId);
        }

        if (update == WebhookStatusUpdate.SUCCESS) {
            applyFinalStatus(payment, PaymentStatus.SUCCESS);
        } else if (update == WebhookStatusUpdate.FAILED) {
            applyFinalStatus(payment, PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

    private void applyFinalStatus(Payment payment, PaymentStatus newStatus) {
        PaymentStatus current = payment.getStatus();
        if (current == PaymentStatus.SUCCESS || current == PaymentStatus.FAILED) {
            return;
        }
        payment.setStatus(newStatus);
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

