package com.sougata.form_data_service.form_schema.exception;

import java.util.UUID;

public class FormNotFoundException extends RuntimeException {
    public FormNotFoundException(UUID formId) {
        super("Form not found with ID: " + formId.toString());
    }
}
