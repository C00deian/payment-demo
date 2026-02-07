package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.PaymentRequest;
import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.model.PaymentStatus;
import com.payment.payment_integration_service.provider.PaymentProvider;
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

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setAmount(req.getAmount());
        payment.setCurrency(req.getCurrency());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setProvider(req.getPaymentProvider());

        repo.save(payment);

        PaymentProvider provider =
                providerRouter.route(payment.getProvider());

        return provider.createPayment(payment);
    }


}
