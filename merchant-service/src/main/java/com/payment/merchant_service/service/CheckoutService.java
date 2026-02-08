package com.payment.merchant_service.service;

import com.payment.merchant_service.client.PaymentIntegrationClient;
import com.payment.merchant_service.dto.CheckoutRequest;
import com.payment.merchant_service.dto.CheckoutResponse;
import com.payment.merchant_service.dto.PaymentResponse;
import com.payment.merchant_service.mapper.CheckoutMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final PaymentIntegrationClient paymentClient;
    private final CheckoutMapper mapper;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var paymentResponse =
                paymentClient.createPayment(mapper.toPaymentRequest(request));

        return mapper.toCheckoutResponse(paymentResponse);
    }
}
