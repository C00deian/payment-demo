package com.payment.stripe_provider_service.controller;


import com.payment.stripe_provider_service.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    private final StripeWebhookService service;

    public StripeWebhookController(StripeWebhookService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> handle(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

        try {
            service.process(payload, signature);
            return ResponseEntity.ok("received");
        } catch (SignatureVerificationException e) {
            // ❌ invalid signature → Stripe should NOT retry
            return ResponseEntity.status(400).body("invalid signature");
        } catch (Exception e) {
            // ❌ transient failure → Stripe MAY retry
            return ResponseEntity.status(500).body("internal error");
        }
    }
}
