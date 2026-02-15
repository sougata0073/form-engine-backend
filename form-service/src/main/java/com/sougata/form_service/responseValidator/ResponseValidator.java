package com.sougata.form_service.responseValidator;

import com.sougata.form_service.dto.validation.request.ValidationRequest;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;

public interface ResponseValidator <V extends ValidationRequest, C extends ValidationConfig> {
    boolean isValid(V validationRequestDto, C validationConfig);
    Class<V> getValidationRequestClass();
    Class<C> getValidationConfigClass();
}
