package com.sougata.form_service.service;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;

import java.util.UUID;

public interface QuestionService {
    QuestionRes createQuestion(UUID formId, QuestionAddUpdateReq dto);

    QuestionRes updateQuestion(UUID formId, Long questionId, QuestionAddUpdateReq dto);

    SuccessMessageDto deleteQuestion(UUID formId, Long questionId, QuestionType questionType);

    QuestionRes getQuestion(UUID formId, Long questionId);

    QuestionSummariesResDto getQuestionSummaries(UUID formId);

    QuestionSummaryDto getQuestionSummary(UUID formId, Long questionId);
}
