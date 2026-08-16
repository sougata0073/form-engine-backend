package com.sougata.form_service.service.impl;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.question.QuestionSummariesResDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.request.QuestionOrderUpdateReqDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.feignClient.FormDataServiceFeignClient;
import com.sougata.form_service.projection.QuestionSummaryProjection;
import com.sougata.form_service.repository.AnyTypeQuestionRepositoryFactory;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.QuestionService;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionManagerFactory questionManagerFactory;
    private final FormDataServiceFeignClient formDataServiceFeignClient;
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionManagerFactory questionManagerFactory, FormDataServiceFeignClient formDataServiceFeignClient, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository) {
        this.questionManagerFactory = questionManagerFactory;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionRes createQuestion(UUID formId, QuestionAddUpdateReq dto) {
        var questionManager = questionManagerFactory.get(dto.getQuestionType());
        return questionManager.create(formId, dto);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public QuestionRes updateQuestion(UUID formId, Long questionId, QuestionAddUpdateReq dto) {

        var prevQType = questionRepository.findQuestionTypeById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId))
                .getQuestionType();

        if (prevQType == dto.getQuestionType()) {
            var manager = questionManagerFactory.get(prevQType);

            return manager.update(formId, questionId, dto);
        } else {
            var prevManager = questionManagerFactory.get(prevQType);
            var newManager = questionManagerFactory.get(dto.getQuestionType());

            prevManager.delete(formId, questionId);

            return newManager.create(formId, questionId, dto);
        }
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public SuccessMessageDto deleteQuestion(UUID formId, Long questionId) {

        var question = questionRepository.findQuestionSummaryById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        // TODO: Message broker will handle this
        formDataServiceFeignClient.deleteResponses(formId, questionId);

        questionRepository.deleteQuestion(questionId);

        questionRepository.setQuestionOrderAfterDeleteQuestion(formId, question.getOrderIndex());

        return SuccessMessageDto.create("Question deleted successfully with question ID: " + questionId);
    }

    @Override
    public QuestionRes getQuestion(UUID formId, Long questionId) {

        var qType = questionRepository.findQuestionTypeById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId))
                .getQuestionType();

        var manager = questionManagerFactory.get(qType);

        return manager.get(formId, questionId);
    }

    @Override
    public QuestionSummariesResDto getQuestionSummaries(UUID formId) {
        var questionProjections = new ArrayList<>(
                questionRepository.findQuestionSummariesByFormId(formId)
        );

        questionProjections.sort(Comparator.comparingInt(QuestionSummaryProjection::getOrderIndex));

        var questions = questionProjections
                .stream()
                .map(q ->
                        new QuestionSummaryDto(q.getId(), q.getQuestion(), q.getQuestionType(), q.getOrderIndex())
                ).toList();

        return new QuestionSummariesResDto(questions);
    }

    @Override
    public QuestionSummaryDto getQuestionSummary(UUID formId, Long questionId) {

        var q = questionRepository.findQuestionSummaryById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        return new QuestionSummaryDto(q.getId(), q.getQuestion(), q.getQuestionType(), q.getOrderIndex());
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public SuccessMessageDto updateOrderIndex(UUID formId, Long questionId, QuestionOrderUpdateReqDto req) {


        var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        var prevIndex = question.getOrderIndex();

        if (!Objects.equals(req.getCurrentIndex(), prevIndex)) {
            question.setOrderIndex(req.getCurrentIndex());

            questionRepository.save(question);
            questionRepository.updateNextQuestionOrderIndexes(formId, questionId, prevIndex, req.getCurrentIndex());
        }

        return SuccessMessageDto.create(
                "Question order updated successfully previous order: " + prevIndex + ". current index : " + req.getCurrentIndex()
        );
    }
}
