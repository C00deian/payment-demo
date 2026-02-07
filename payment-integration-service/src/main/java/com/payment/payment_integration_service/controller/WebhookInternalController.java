package com.payment.payment_integration_service.controller;

import com.payment.payment_integration_service.service.PaymentStatusService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/payments")
public class WebhookInternalController {

    private final PaymentStatusService paymentStatusService;

    public WebhookInternalController(PaymentStatusService paymentStatusService) {
        this.paymentStatusService = paymentStatusService;
    }

    @PostMapping("/{paymentId}/success")
    public void success(@PathVariable String paymentId) {
        paymentStatusService.markSuccess(paymentId);
    }

    @PostMapping("/{paymentId}/failed")
    public void failed(@PathVariable String paymentId) {
        paymentStatusService.markFailed(paymentId);
    }
}
