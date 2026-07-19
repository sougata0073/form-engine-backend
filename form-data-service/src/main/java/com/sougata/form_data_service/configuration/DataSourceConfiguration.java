package com.sougata.form_data_service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean(name = "formDataServiceDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.form-data-service")
    public DataSource formDataServiceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "formSchemaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.form-schema")
    public DataSource formSchemaDataSource() {
        return DataSourceBuilder.create().build();
    }

}
