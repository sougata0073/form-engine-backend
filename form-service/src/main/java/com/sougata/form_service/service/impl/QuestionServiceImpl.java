package com.sougata.form_service.service.impl;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.feignClient.FormDataServiceFeignClient;
import com.sougata.form_service.projection.QuestionSummaryProjection;
import com.sougata.form_service.repository.QuestionRepositoryFactory;
import com.sougata.form_service.service.QuestionService;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionManagerFactory questionManagerFactory;
    private final FormDataServiceFeignClient formDataServiceFeignClient;
    private final QuestionRepositoryFactory questionRepositoryFactory;

    @Autowired
    public QuestionServiceImpl(QuestionManagerFactory questionManagerFactory, FormDataServiceFeignClient formDataServiceFeignClient, QuestionRepositoryFactory questionRepositoryFactory) {
        this.questionManagerFactory = questionManagerFactory;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
        this.questionRepositoryFactory = questionRepositoryFactory;
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
                    manager.delete(questionId);

                    return matchingService.create(formId, questionId, dto);
                }
            }

            throw new QuestionNotFoundException(dto.getQuestionType(), questionId);
        }
    }

    @Override
    public SuccessMessageDto deleteQuestion(UUID formId, Long questionId, QuestionType questionType) {

        formDataServiceFeignClient.deleteResponses(formId, questionId, questionType);

        var manager = questionManagerFactory.get(questionType);
        manager.delete(questionId);

        return SuccessMessageDto.create("Question deleted successfully");
    }

    @Override
    public QuestionRes getQuestion(UUID formId, Long questionId) {
        QuestionRes questionRes = null;

        for (var repo : questionManagerFactory.getAll()) {
            try {
                questionRes = repo.get(formId, questionId);
                break;
            } catch (QuestionNotFoundException ignored) {
            }
        }

        if (questionRes == null) {
            throw new QuestionNotFoundException(questionId);
        }

        return questionRes;
    }

    @Override
    public QuestionSummariesResDto getQuestionSummaries(UUID formId) {
        var questionProjections = new ArrayList<QuestionSummaryProjection>();
        var questionTypeMap = new HashMap<Long, QuestionType>();

        questionRepositoryFactory.getAll().forEach(repo -> {
            var ques = repo.findQuestionSummariesByFormId(formId);

            ques.forEach(q -> questionTypeMap.put(q.getId(), repo.getQuestionType()));

            questionProjections.addAll(ques);
        });

        questionProjections.sort(Comparator.comparingInt(QuestionSummaryProjection::getOrderIndex));

        var questions = questionProjections
                .stream()
                .map(q ->
                        new QuestionSummaryDto(q.getId(), q.getQuestion(), questionTypeMap.get(q.getId()), q.getOrderIndex())
                ).toList();

        return new QuestionSummariesResDto(questions);
    }

    @Override
    public QuestionSummaryDto getQuestionSummary(UUID formId, Long questionId) {
        QuestionSummaryDto summary = null;

        for (var repo : questionRepositoryFactory.getAll()) {
            var qOptional = repo.findQuestionSummaryByFormIdAndId(formId, questionId);

            if (qOptional.isPresent()) {
                var q = qOptional.get();
                summary = new QuestionSummaryDto(q.getId(), q.getQuestion(), repo.getQuestionType(), q.getOrderIndex());

                break;
            }
        }

        if (summary == null) {
            throw new QuestionNotFoundException(questionId);
        }

        return summary;
    }
}
