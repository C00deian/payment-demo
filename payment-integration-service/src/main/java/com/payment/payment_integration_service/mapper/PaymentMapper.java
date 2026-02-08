package com.payment.payment_integration_service.mapper;

import com.payment.payment_integration_service.dto.PaymentDetailsResponse;
import com.payment.payment_integration_service.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDetailsResponse toDetails(Payment payment);
}

