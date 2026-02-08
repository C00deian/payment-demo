package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.PaymentRequest;
import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.model.PaymentStatus;
import com.payment.payment_integration_service.provider.PaymentProvider;
import com.payment.payment_integration_service.provider.PaymentInitiationResult;
import com.payment.payment_integration_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
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

        payment.setStatus(PaymentStatus.PENDING);
        payment.setProviderSessionId(result.getProviderSessionId());
        repo.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setRedirectUrl(result.getRedirectUrl());
        response.setStatus(payment.getStatus().name());
        return response;
    }


}
