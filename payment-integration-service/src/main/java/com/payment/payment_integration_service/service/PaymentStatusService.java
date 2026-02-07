package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.model.PaymentStatus;
import com.payment.payment_integration_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentStatusService {

    private final PaymentRepository repo;

    public PaymentStatusService(PaymentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void markSuccess(String paymentId) {

        Payment payment = repo.findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new IllegalStateException("Payment not found: " + paymentId)
                );

        // 🔁 Idempotency
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        repo.save(payment);
    }

    @Transactional
    public void markFailed(String paymentId) {

        Payment payment = repo.findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new IllegalStateException("Payment not found: " + paymentId)
                );

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        repo.save(payment);
    }
}
