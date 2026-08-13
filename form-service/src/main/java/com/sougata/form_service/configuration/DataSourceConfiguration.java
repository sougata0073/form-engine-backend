package com.sougata.form_service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean(name = "schemaDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.schema")
    public DataSource schemaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "templateDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.template")
    public DataSource templateDataSource() {
        return DataSourceBuilder.create().build();
    }

}
