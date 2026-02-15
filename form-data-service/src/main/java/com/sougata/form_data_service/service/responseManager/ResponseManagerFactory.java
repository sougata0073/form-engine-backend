package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.QuestionResponseAddReq;
import com.sougata.form_data_service.exception.NoResponseManagerFoundException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ResponseManagerFactory {

    private final ApplicationContext applicationContext;

    public ResponseManagerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <QR extends QuestionResponseAddReq> ResponseManager<QR> getResponseManager(QuestionType questionType) {
        try {
            return applicationContext.getBean(
                    String.format("%s_RESPONSE_MANAGER", questionType.name()),
                    ResponseManager.class
            );
        } catch (BeansException e) {
            throw new NoResponseManagerFoundException(questionType);
        }
    }

}
