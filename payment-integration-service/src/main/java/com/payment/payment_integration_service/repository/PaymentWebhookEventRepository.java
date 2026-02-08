package com.payment.payment_integration_service.repository;

import com.payment.payment_integration_service.model.PaymentWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWebhookEventRepository extends JpaRepository<PaymentWebhookEvent, Long> {
}
