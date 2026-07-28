package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionResponse;
import com.sougata.form_data_service.dto.response.question.ResponseByQuestionSummary;
import com.sougata.form_data_service.dto.response.question.ResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.QuestionResponse;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ResponseManager<
        QR extends QuestionResponseAddReq,
        RS extends ResponseSummaryDto,
        QRes extends QuestionRes,
        ResByQ extends ResponseQuestionDto<ResByQRes>,
        ResByQRes extends ResponseByQuestionResponse,
        ResByQSumm extends ResponseByQuestionSummary
        > {

    private final QuestionResponseRepository questionResponseRepository;

    public ResponseManager(QuestionResponseRepository questionResponseRepository) {
        this.questionResponseRepository = questionResponseRepository;
    }

    public abstract void create(QR response, FormResponse formResponse);

    public abstract List<RS> getResponseSummaries(UUID formId, List<QRes> questionResponses);

    public abstract ResByQSumm getResponseByQuestionSummary(UUID formId, QRes questionResponse);

    public abstract ResByQ getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable);

    public abstract QuestionType getQuestionType();

    public abstract void deleteResponses(UUID formId, Long questionId);

    public QuestionResponse createQuestionResponse(Long questionId, FormResponse formResponse) {
        var qr = new QuestionResponse();

        qr.setQuestionId(questionId);
        qr.setFormResponse(formResponse);
        qr.setQuestionType(getQuestionType());

        return questionResponseRepository.save(qr);
    }

    public Long getTotalResponseCount(UUID formId, Long questionId) {
        return questionResponseRepository.getTotalResponseCount(formId, questionId);
    }

}
