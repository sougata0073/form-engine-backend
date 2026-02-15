package com.sougata.form_service.responseValidator;

import com.sougata.form_service.constant.ValidationId;
import com.sougata.form_service.dto.validation.request.ValidationRequest;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.NoResponseValidatorFoundException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ResponseValidatorFactory {

    private final ApplicationContext applicationContext;

    public ResponseValidatorFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <V extends ValidationRequest, C extends ValidationConfig> ResponseValidator<V, C> getValidator(
            ValidationId validationId
    ) {
        String validatorBeanName = String.format("%s_RESPONSE_VALIDATOR", validationId.name());

        try {
            return applicationContext.getBean(validatorBeanName, ResponseValidator.class);
        } catch (BeansException e) {
            throw new NoResponseValidatorFoundException(validatorBeanName);
        }
    }

}
