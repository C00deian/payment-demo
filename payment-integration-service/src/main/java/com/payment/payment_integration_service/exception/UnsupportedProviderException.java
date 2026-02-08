package com.payment.payment_integration_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedProviderException extends RuntimeException {

    private final String provider;

    public UnsupportedProviderException(String provider) {
        super("Unsupported payment provider: " + provider);
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}
