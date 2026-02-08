package com.payment.payment_integration_service.service;

import com.payment.payment_integration_service.dto.PaymentDetailsResponse;
import com.payment.payment_integration_service.exception.PaymentNotFoundException;
import com.payment.payment_integration_service.mapper.PaymentMapper;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final PaymentRepository repo;
    private final PaymentMapper mapper;

    public PaymentDetailsResponse getByPaymentId(String paymentId) {
        Payment payment = repo.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return mapper.toDetails(payment);
    }

    public List<PaymentDetailsResponse> recent() {
        return repo.findTop20ByOrderByIdDesc()
                .stream()
                .map(mapper::toDetails)
                .toList();
    }
}
