package com.sougata.form_data_service.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JpaConfiguration {

    @Bean(name = "formDataServiceEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean formDataServiceEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("formDataServiceDataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.sougata.form_data_service.model")
                .persistenceUnit("form-data-service")
                .properties(properties)
                .build();
    }

    @Bean(name = "formSchemaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean formSchemaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("formSchemaDataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.sougata.form_data_service.form_schema.model")
                .persistenceUnit("form-schema")
                .properties(properties)
                .build();
    }

    @Bean(name = "formDataServiceTransactionManager")
    @Primary
    public PlatformTransactionManager formDataServiceTransactionManager(
            @Qualifier("formDataServiceEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }

    @Bean(name = "formSchemaTransactionManager")
    public PlatformTransactionManager formSchemaTransactionManager(
            @Qualifier("formSchemaEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }

}
