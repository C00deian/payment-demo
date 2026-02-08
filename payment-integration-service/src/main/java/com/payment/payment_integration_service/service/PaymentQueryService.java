package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.PaymentDetailsResponse;
import com.payment.payment_integration_service.exception.PaymentNotFoundException;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentQueryService {

    private final PaymentRepository repo;

    public PaymentQueryService(PaymentRepository repo) {
        this.repo = repo;
    }

    public PaymentDetailsResponse getByPaymentId(String paymentId) {
        Payment payment = repo.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return map(payment);
    }

    public List<PaymentDetailsResponse> recent() {
        return repo.findTop20ByOrderByIdDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    private PaymentDetailsResponse map(Payment payment) {
        PaymentDetailsResponse response = new PaymentDetailsResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setProvider(payment.getProvider());
        response.setProviderSessionId(payment.getProviderSessionId());
        response.setProviderPaymentIntentId(payment.getProviderPaymentIntentId());
        response.setStatus(payment.getStatus());
        return response;
    }
}
