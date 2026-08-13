package com.sougata.form_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sougata.form_service.repository",
        entityManagerFactoryRef = "schemaEntityManagerFactory",
        transactionManagerRef = "schemaTransactionManager"
)
public class FormSchemaRepositoryConfiguration {
}
