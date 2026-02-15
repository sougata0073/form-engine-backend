package com.sougata.form_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FormServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormServiceApplication.class, args);
    }

}
