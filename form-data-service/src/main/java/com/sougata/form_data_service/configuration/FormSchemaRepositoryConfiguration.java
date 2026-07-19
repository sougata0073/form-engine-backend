package com.sougata.form_data_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sougata.form_data_service.form_schema.repository",
        entityManagerFactoryRef = "formSchemaEntityManagerFactory",
        transactionManagerRef = "formSchemaTransactionManager"
)
public class FormSchemaRepositoryConfiguration {
}
