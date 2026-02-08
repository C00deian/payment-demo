package com.payment.payment_integration_service.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "DATABASE_URL")
public class DatabaseUrlDataSourceConfig {

    @Bean
    public DataSource dataSource(Environment environment) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(databaseUrl)) {
            throw new IllegalStateException("DATABASE_URL is set but empty");
        }

        DatabaseUrlInfo info = DatabaseUrlInfo.parse(databaseUrl.trim());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(info.jdbcUrl);
        if (StringUtils.hasText(info.username)) config.setUsername(info.username);
        if (StringUtils.hasText(info.password)) config.setPassword(info.password);

        return new HikariDataSource(config);
    }

    private static final class DatabaseUrlInfo {
        private final String jdbcUrl;
        private final String username;
        private final String password;

        private DatabaseUrlInfo(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }

        static DatabaseUrlInfo parse(String databaseUrl) {
            if (databaseUrl.startsWith("jdbc:")) {
                return new DatabaseUrlInfo(databaseUrl, null, null);
            }

            if (!(databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
                throw new IllegalArgumentException("Unsupported DATABASE_URL scheme (expected postgres:// or postgresql://)");
            }

            URI uri = URI.create(databaseUrl);

            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String dbName = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");

            if (!StringUtils.hasText(host) || !StringUtils.hasText(dbName)) {
                throw new IllegalArgumentException("Invalid DATABASE_URL (missing host or database name)");
            }

            String query = uri.getRawQuery();
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName + (query == null ? "" : "?" + query);

            String username = null;
            String password = null;
            String userInfo = uri.getRawUserInfo();
            if (StringUtils.hasText(userInfo)) {
                String[] parts = userInfo.split(":", 2);
                username = urlDecode(parts[0]);
                password = parts.length > 1 ? urlDecode(parts[1]) : null;
            }

            return new DatabaseUrlInfo(jdbcUrl, username, password);
        }

        private static String urlDecode(String value) {
            if (!StringUtils.hasText(value)) return value;
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
    }
}

