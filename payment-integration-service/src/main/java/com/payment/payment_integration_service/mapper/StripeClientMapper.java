package com.payment.payment_integration_service.mapper;

import com.payment.payment_integration_service.client.stripe.StripeCreatePaymentClientRequest;
import com.payment.payment_integration_service.client.stripe.StripePaymentClientResponse;
import com.payment.payment_integration_service.model.Payment;
import com.payment.payment_integration_service.provider.PaymentInitiationResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StripeClientMapper {

    @Mapping(target = "successUrl", ignore = true)
    @Mapping(target = "failureUrl", ignore = true)
    StripeCreatePaymentClientRequest toCreateRequest(Payment payment);

    @Mapping(target = "redirectUrl", source = "checkoutUrl")
    @Mapping(target = "providerSessionId", source = "stripeSessionId")
    PaymentInitiationResult toInitiationResult(StripePaymentClientResponse response);
}

