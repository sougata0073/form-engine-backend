package com.sougata.form_data_service.form_schema.exception;

import java.util.List;

public class RequiredQuestionResponseNotFoundException extends RuntimeException {
    public RequiredQuestionResponseNotFoundException(List<String> missingQuestionIds) {
        super(String.join(", ", missingQuestionIds));
    }
}
