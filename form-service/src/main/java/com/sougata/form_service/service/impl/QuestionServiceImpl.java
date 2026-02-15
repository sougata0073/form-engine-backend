package com.sougata.form_service.service.impl;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.service.QuestionService;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionManagerFactory questionManagerFactory;

    @Autowired
    public QuestionServiceImpl(QuestionManagerFactory questionManagerFactory) {
        this.questionManagerFactory = questionManagerFactory;
    }

    @Override
    public QuestionRes createQuestion(UUID formId, QuestionAddUpdateReq dto) {
        var questionManager = questionManagerFactory.get(dto.getQuestionType());
        return questionManager.create(formId, dto);
    }

    @Override
    @Transactional
    public QuestionRes updateQuestion(UUID formId, Long questionId, QuestionAddUpdateReq dto) {
        var matchingService = questionManagerFactory.get(dto.getQuestionType());

        if (matchingService.exists(questionId)) {
            return matchingService.update(questionId, dto);
        } else {
            for (var manager : questionManagerFactory.getAll()) {
                if (manager.exists(questionId)) {
                    System.out.println(dto);
                    manager.delete(questionId);

                    return matchingService.create(formId, questionId, dto);
                }
            }

            throw new QuestionNotFoundException(dto.getQuestionType(), questionId);
        }
    }

    @Override
    public SuccessMessageDto deleteQuestion(UUID formId, Long questionId, QuestionType questionType) {
        var manager = questionManagerFactory.get(questionType);
        manager.delete(questionId);

        return SuccessMessageDto.create("Question deleted successfully");
    }
}
