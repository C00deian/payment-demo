package com.payment.stripe_provider_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalApiKeyFeignConfig {

    @Value("${internal.api.key:}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor internalApiKeyInterceptor() {
        return template -> {
            if (internalApiKey != null && !internalApiKey.isBlank()) {
                template.header("X-Internal-Api-Key", internalApiKey);
            }
        };
    }
}

