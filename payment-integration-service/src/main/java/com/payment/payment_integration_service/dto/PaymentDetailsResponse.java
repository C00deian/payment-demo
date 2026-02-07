package com.payment.payment_integration_service.dto;

import com.payment.payment_integration_service.model.PaymentStatus;
import lombok.Data;

@Data
public class PaymentDetailsResponse {
    private String paymentId;
    private String orderId;
    private Long amount;
    private String currency;
    private String provider;
    private PaymentStatus status;
}

