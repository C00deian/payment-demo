package com.payment.merchant_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(Environment environment) {
        CorsConfiguration config = new CorsConfiguration();

        String allowedOriginPatternsRaw = environment.getProperty("CORS_ALLOWED_ORIGIN_PATTERNS", "");
        List<String> allowedOriginPatterns = parseCsv(allowedOriginPatternsRaw);
        if (!allowedOriginPatterns.isEmpty()) {
            config.setAllowedOriginPatterns(allowedOriginPatterns);
        } else {
            String allowedOriginsRaw = environment.getProperty("CORS_ALLOWED_ORIGINS", "");
            List<String> allowedOrigins = parseCsv(allowedOriginsRaw);
            if (allowedOrigins.isEmpty()) {
                allowedOrigins = List.of(
                        "http://localhost:5173",
                        "http://localhost:3000"
                );
            }
            config.setAllowedOrigins(allowedOrigins);
        }
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private static List<String> parseCsv(String value) {
        if (value == null || value.trim().isEmpty()) return List.of();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
