package com.payment.payment_integration_service.controller;

import com.payment.payment_integration_service.dto.StripeWebhookUpdateRequest;
import com.payment.payment_integration_service.service.StripeWebhookUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/webhooks")
public class InternalStripeWebhookController {

    private final StripeWebhookUpdateService service;

    public InternalStripeWebhookController(StripeWebhookUpdateService service) {
        this.service = service;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> stripe(@RequestBody StripeWebhookUpdateRequest request) {
        service.apply(request);
        return ResponseEntity.ok().build();
    }
}

