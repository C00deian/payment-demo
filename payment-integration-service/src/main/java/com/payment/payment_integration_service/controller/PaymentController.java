package com.payment.payment_integration_service.controller;

import com.payment.payment_integration_service.dto.PaymentDetailsResponse;
import com.payment.payment_integration_service.dto.PaymentRequest;
import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.service.PaymentQueryService;
import com.payment.payment_integration_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final PaymentQueryService queryService;

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return service.createPayment(request);
    }

    @GetMapping("/{paymentId}")
    public PaymentDetailsResponse get(@PathVariable String paymentId) {
        return queryService.getByPaymentId(paymentId);
    }

    @GetMapping("/recent")
    public List<PaymentDetailsResponse> recent() {
        return queryService.recent();
    }
}
