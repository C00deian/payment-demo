package com.payment.merchant_service.service;

import com.payment.merchant_service.client.PaymentIntegrationClient;
import com.payment.merchant_service.dto.CheckoutRequest;
import com.payment.merchant_service.dto.CheckoutResponse;
import com.payment.merchant_service.dto.PaymentRequest;
import com.payment.merchant_service.dto.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    public PaymentRequest mapToPaymentRequest(CheckoutRequest request) {
        return PaymentRequest.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .orderId(request.getOrderId())
                .paymentProvider(request.getPaymentProvider())
                .build();
    }

    private final PaymentIntegrationClient paymentClient;

    public CheckoutService(PaymentIntegrationClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public CheckoutResponse checkout(CheckoutRequest request) {

        PaymentRequest paymentRequest = mapToPaymentRequest(request);

        PaymentResponse paymentResponse =
                paymentClient.createPayment(paymentRequest);

        CheckoutResponse response = new CheckoutResponse();
        response.setRedirectUrl(paymentResponse.getRedirectUrl());
        return response;
    }
}
