package com.payment.payment_integration_service.config;

import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, org.springframework.boot.SpringApplication application) {
        boolean hasExplicitUrl = StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_URL"));
        boolean hasExplicitUsername = StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_USERNAME"));
        boolean hasExplicitPassword = StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_PASSWORD"));
        if (hasExplicitUrl) return;

        String databaseUrl = firstNonBlank(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("RENDER_DATABASE_URL"),
                environment.getProperty("RENDER_POSTGRES_URL")
        );
        if (!StringUtils.hasText(databaseUrl)) {
            return;
        }

        Map<String, Object> overrides = new LinkedHashMap<>();

        if (databaseUrl.startsWith("jdbc:")) {
            overrides.put("spring.datasource.url", databaseUrl);
            applyOverrides(environment, overrides);
            return;
        }

        String trimmed = databaseUrl.trim();
        if (!(trimmed.startsWith("postgres://") || trimmed.startsWith("postgresql://"))) {
            return;
        }

        URI uri = URI.create(trimmed);

        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String dbName = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
        if (!StringUtils.hasText(host) || !StringUtils.hasText(dbName)) {
            return;
        }

        String query = uri.getRawQuery();
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + (query == null ? "" : "?" + query);
        overrides.put("spring.datasource.url", jdbcUrl);

        String userInfo = uri.getRawUserInfo();
        if (StringUtils.hasText(userInfo)) {
            String[] parts = userInfo.split(":", 2);
            String username = urlDecode(parts[0]);
            String password = parts.length > 1 ? urlDecode(parts[1]) : null;

            if (!hasExplicitUsername && StringUtils.hasText(username)) {
                overrides.put("spring.datasource.username", username);
            }
            if (!hasExplicitPassword && StringUtils.hasText(password)) {
                overrides.put("spring.datasource.password", password);
            }
        }

        applyOverrides(environment, overrides);
    }

    private static void applyOverrides(ConfigurableEnvironment environment, Map<String, Object> overrides) {
        if (overrides.isEmpty()) return;
        environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlOverrides", overrides));
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value == null) continue;
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) return trimmed;
        }
        return null;
    }

    private static String urlDecode(String value) {
        if (!StringUtils.hasText(value)) return value;
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
