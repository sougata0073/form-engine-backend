package com.sougata.form_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sougata.form_service.template.repository",
        entityManagerFactoryRef = "templateEntityManagerFactory",
        transactionManagerRef = "templateTransactionManager"
)
public class FormTemplateRepositoryConfiguration {
}
