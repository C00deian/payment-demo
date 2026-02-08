package com.payment.payment_integration_service.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(
        name = "payment_webhook_events",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerEventId"})
)
public class PaymentWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String providerEventId;

    @Column(nullable = false)
    private String eventType;

    private String objectId;

    @Column(nullable = false)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatusUpdate statusUpdate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant receivedAt;
}
