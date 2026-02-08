package com.payment.merchant_service.mapper;

import com.payment.merchant_service.dto.CheckoutRequest;
import com.payment.merchant_service.dto.CheckoutResponse;
import com.payment.merchant_service.dto.PaymentRequest;
import com.payment.merchant_service.dto.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CheckoutMapper {
    PaymentRequest toPaymentRequest(CheckoutRequest request);
    CheckoutResponse toCheckoutResponse(PaymentResponse response);
}

