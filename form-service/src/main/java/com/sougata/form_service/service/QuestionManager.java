package com.sougata.form_service.service;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.dto.validation.request.ValidationRequest;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.QuestionRepository;

import java.util.UUID;

public abstract class
QuestionManager<QAUR extends QuestionAddUpdateReq, QR extends QuestionRes, V extends ValidationRequest> {

    public abstract QR get(UUID formId, Long questionId);

    public abstract QR create(UUID formId, QAUR crudDto);

    public abstract QR create(UUID formId, Long questionId, QAUR crudDto);

    public abstract QR update(Long questionId, QAUR crudDto);

    public abstract boolean validateResponse(V validationDto);

    public abstract Class<QAUR> getCrudDtoClass();

    public abstract Class<V> getValidationDtoClass();

    public abstract <Q extends Question, ID, QRD extends QuestionRes> QuestionRepository<Q, ID, QRD> getQuestionRepository();

    public abstract QuestionType getQuestionType();

    public void delete(Long questionId) {
        getQuestionRepository().deleteById(questionId);
    }

    public boolean exists(Long questionId) {
        return getQuestionRepository().existsById(questionId);
    }

}
