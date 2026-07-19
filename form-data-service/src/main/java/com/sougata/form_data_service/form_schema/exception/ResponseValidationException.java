package com.sougata.form_data_service.form_schema.exception;

public class ResponseValidationException extends RuntimeException {
    public ResponseValidationException(String message) {
        super(message);
    }
}
