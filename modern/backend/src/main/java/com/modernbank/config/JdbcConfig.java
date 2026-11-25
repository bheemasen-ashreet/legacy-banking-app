package com.modernbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.dialect.AnsiDialect;
import org.springframework.data.relational.core.dialect.Dialect;

/**
 * Registers ANSI SQL dialect for Spring Data JDBC.
 * SQLite is not natively recognised by the dialect resolver, so we provide
 * AnsiDialect explicitly — it covers all basic CRUD operations we need.
 */
@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    @Override
    public Dialect jdbcDialect(org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations operations) {
        return AnsiDialect.INSTANCE;
    }
}
