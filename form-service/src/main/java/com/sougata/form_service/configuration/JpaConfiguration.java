package com.sougata.form_service.configuration;

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

    @Bean(name = "schemaEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean schemaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("schemaDataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.sougata.form_service.model")
                .persistenceUnit("form-schema")
                .properties(properties)
                .build();
    }

    @Bean(name = "templateEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean templateEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("templateDataSource") DataSource dataSource
    ) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.sougata.form_service.template.model")
                .persistenceUnit("form-schema")
                .properties(properties)
                .build();
    }

    @Bean(name = "schemaTransactionManager")
    @Primary
    public PlatformTransactionManager schemaTransactionManager(
            @Qualifier("schemaEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }

    @Bean(name = "templateTransactionManager")
    public PlatformTransactionManager templateTransactionManager(
            @Qualifier("templateEntityManagerFactory") EntityManagerFactory factory
    ) {
        return new JpaTransactionManager(factory);
    }

}
