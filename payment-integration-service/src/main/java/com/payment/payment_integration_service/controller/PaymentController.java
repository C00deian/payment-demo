package com.payment.payment_integration_service.controller;

import com.payment.payment_integration_service.dto.PaymentRequest;
import com.payment.payment_integration_service.dto.PaymentResponse;
import com.payment.payment_integration_service.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return service.createPayment(request);
    }
}
