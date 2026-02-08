package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.PaymentRequest;
import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.exception.PaymentNotFoundException;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.model.PaymentStatus;
import com.payment.payment_integration_service.provider.PaymentProvider;
import com.payment.payment_integration_service.provider.PaymentInitiationResult;
import com.payment.payment_integration_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repo;
    private final ProviderRouter providerRouter;

    public PaymentResponse createPayment(PaymentRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (req.getOrderId() == null || req.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (req.getCurrency() == null || req.getCurrency().trim().isEmpty()) {
            throw new IllegalArgumentException("currency is required");
        }
        if (req.getPaymentProvider() == null || req.getPaymentProvider().trim().isEmpty()) {
            throw new IllegalArgumentException("paymentProvider is required");
        }

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(req.getOrderId().trim());
        payment.setAmount(req.getAmount());
        payment.setCurrency(req.getCurrency().trim().toLowerCase());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setProvider(req.getPaymentProvider().trim().toUpperCase());

        repo.save(payment);

        PaymentProvider provider =
                providerRouter.route(payment.getProvider());

        PaymentInitiationResult result = provider.initiatePayment(payment);

        Payment updated = applyProviderInitiation(payment.getPaymentId(), result);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(updated.getPaymentId());
        response.setRedirectUrl(result.getRedirectUrl());
        response.setStatus(updated.getStatus().name());
        return response;
    }

    private Payment applyProviderInitiation(String paymentId, PaymentInitiationResult result) {
        for (int attempt = 0; attempt < 2; attempt++) {
            Payment current = repo.findByPaymentId(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException(paymentId));

            if (current.getStatus() != PaymentStatus.SUCCESS && current.getStatus() != PaymentStatus.FAILED) {
                current.setStatus(PaymentStatus.PENDING);
            }

            if (current.getProviderSessionId() == null && result.getProviderSessionId() != null) {
                current.setProviderSessionId(result.getProviderSessionId());
            }

            try {
                return repo.save(current);
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == 1) {
                    return repo.findByPaymentId(paymentId)
                            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
                }
            }
        }

        return repo.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

}
