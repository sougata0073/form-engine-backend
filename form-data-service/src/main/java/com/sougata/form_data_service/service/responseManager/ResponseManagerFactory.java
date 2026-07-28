package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.exception.NoResponseManagerFoundException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseManagerFactory {

    private final ApplicationContext applicationContext;

    public ResponseManagerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <
            QR extends QuestionResponseAddReq,
            RS extends ResponseSummaryDto,
            QRes extends QuestionRes,
            ResByQ extends ResponseQuestionDto<ResByQRes>,
            ResByQRes extends ResponseByQuestionResponse,
            ResByQSumm extends ResponseByQuestionSummary
            >
    ResponseManager<QR, RS, QRes, ResByQ, ResByQRes, ResByQSumm> get(QuestionType questionType) {
        try {
            return applicationContext.getBean(
                    String.format("%s_RESPONSE_MANAGER", questionType.name()),
                    ResponseManager.class
            );
        } catch (BeansException e) {
            throw new NoResponseManagerFoundException(questionType);
        }
    }

    @SuppressWarnings("unchecked")
    public <
            QR extends QuestionResponseAddReq,
            RS extends ResponseSummaryDto,
            QRes extends QuestionRes,
            ResByQ extends ResponseQuestionDto<ResByQRes>,
            ResByQRes extends ResponseByQuestionResponse,
            ResByQSumm extends ResponseByQuestionSummary
            >
    List<ResponseManager<QR, RS, QRes, ResByQ, ResByQRes, ResByQSumm>> getAll() {
        List<ResponseManager<QR, RS, QRes, ResByQ, ResByQRes, ResByQSumm>> repos = new ArrayList<>();

        for (QuestionType questionType : QuestionType.values()) {
            var repo = applicationContext.getBean(
                    String.format("%s_RESPONSE_MANAGER", questionType.name()),
                    ResponseManager.class
            );

            repos.add(repo);
        }

        return repos;
    }

}
