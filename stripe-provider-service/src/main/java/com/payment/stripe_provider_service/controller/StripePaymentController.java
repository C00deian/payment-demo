package com.payment.stripe_provider_service.controller;

import com.payment.stripe_provider_service.dto.StripeCreatePaymentRequest;
import com.payment.stripe_provider_service.dto.StripePaymentResponse;
import com.payment.stripe_provider_service.service.StripePaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stripe/payments")
public class StripePaymentController {

    private final StripePaymentService service;

    public StripePaymentController(StripePaymentService service) {
        this.service = service;
    }

    @PostMapping
    public StripePaymentResponse create(
            @RequestBody StripeCreatePaymentRequest request) {
        return service.create(request);
    }
}


