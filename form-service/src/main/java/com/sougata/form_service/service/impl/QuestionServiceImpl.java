package com.sougata.form_service.service.impl;

import com.sougata.form_service.configuration.AppConfiguration;
import com.sougata.form_service.constant.CommonCacheNames;
import com.sougata.form_service.constant.cacheNames.FormCacheNames;
import com.sougata.form_service.constant.cacheNames.QuestionCacheNames;
import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.FormResponseDto;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionManagerFactory questionManagerFactory;
    private final FormDataServiceFeignClient formDataServiceFeignClient;
    private final QuestionRepository questionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AppConfiguration appConfiguration;

    @Autowired
    public QuestionServiceImpl(QuestionManagerFactory questionManagerFactory, FormDataServiceFeignClient formDataServiceFeignClient, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository, RedisTemplate<String, Object> redisTemplate, AppConfiguration appConfiguration) {
        this.questionManagerFactory = questionManagerFactory;
        this.formDataServiceFeignClient = formDataServiceFeignClient;
        this.questionRepository = questionRepository;
        this.redisTemplate = redisTemplate;
        this.appConfiguration = appConfiguration;
    }

    @Override
    public QuestionRes createQuestion(UUID formId, QuestionAddUpdateReq dto) {
        var questionManager = questionManagerFactory.get(dto.getQuestionType());
        var question = questionManager.create(formId, dto);

        addQuestionInQuestionSummaries(formId, question);
        addQuestionInFormDetails(formId, question);
        putQuestionDetails(question);
        putQuestionSummary(new QuestionSummaryDto(question.getId(), question.getQuestion(), question.getQuestionType(), question.getOrderIndex()));

        return question;
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public QuestionRes updateQuestion(UUID formId, Long questionId, QuestionAddUpdateReq dto) {

        var prevQType = questionRepository.findQuestionTypeById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId))
                .getQuestionType();

        QuestionRes question;

        if (prevQType == dto.getQuestionType()) {
            var manager = questionManagerFactory.get(prevQType);

            question = manager.update(formId, questionId, dto);
        } else {
            var prevManager = questionManagerFactory.get(prevQType);
            var newManager = questionManagerFactory.get(dto.getQuestionType());

            prevManager.delete(formId, questionId);

            question = newManager.create(formId, questionId, dto);
        }

        updateQuestionInFormDetails(formId, question);
        updateQuestionInQuestionSummaries(formId, question);
        putQuestionDetails(question);
        putQuestionSummary(new QuestionSummaryDto(question.getId(), question.getQuestion(), question.getQuestionType(), question.getOrderIndex()));

        return question;
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

        deleteQuestionInFormDetails(formId, questionId);
        deleteQuestionInQuestionSummaries(formId, questionId);

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

    private void addQuestionInQuestionSummaries(UUID formId, QuestionRes question) {

        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_SUMMARIES + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var prevSummaries = (QuestionSummariesResDto) redisTemplate.opsForValue().get(cacheKey);

            var newSummary = new QuestionSummaryDto(
                    question.getId(),
                    question.getQuestion(),
                    question.getQuestionType(),
                    question.getOrderIndex()
            );

            prevSummaries.getQuestions().add(newSummary);
            prevSummaries.getQuestions().sort(Comparator.comparingInt(QuestionSummaryDto::getOrderIndex));

            redisTemplate.opsForValue().set(cacheKey, prevSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void deleteQuestionInQuestionSummaries(UUID formId, Long questionId) {

        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_SUMMARIES + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var prevSummaries = (QuestionSummariesResDto) redisTemplate.opsForValue().get(cacheKey);

            prevSummaries.getQuestions().removeIf(q -> q.getId().equals(questionId));
            prevSummaries.getQuestions().sort(Comparator.comparingInt(QuestionSummaryDto::getOrderIndex));

            redisTemplate.opsForValue().set(cacheKey, prevSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void updateQuestionInQuestionSummaries(UUID formId, QuestionRes question) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_SUMMARIES + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var prevSummaries = (QuestionSummariesResDto) redisTemplate.opsForValue().get(cacheKey);

            prevSummaries.getQuestions().forEach(q -> {
                if (q.getId().equals(question.getId())) {
                    q.setQuestion(question.getQuestion());
                    q.setQuestionType(question.getQuestionType());
                    q.setOrderIndex(question.getOrderIndex());
                }
            });

            redisTemplate.opsForValue().set(cacheKey, prevSummaries, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void addQuestionInFormDetails(UUID formId, QuestionRes question) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.PREFIX + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(cacheKey);

            formDetails.getQuestions().add(question);
            formDetails.getQuestions().sort(Comparator.comparingInt(QuestionRes::getOrderIndex));

            redisTemplate.opsForValue().set(cacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void updateQuestionInFormDetails(UUID formId, QuestionRes question) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.PREFIX + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(cacheKey);

            formDetails.getQuestions().forEach(q -> {
                if (q.getId().equals(question.getId())) {
                    q.setQuestion(question.getQuestion());
                    q.setDescription(question.getDescription());
                    q.setOrderIndex(question.getOrderIndex());
                    q.setQuestionType(question.getQuestionType());
                    q.setRequired(question.getRequired());
                }
            });

            redisTemplate.opsForValue().set(cacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void deleteQuestionInFormDetails(UUID formId, Long questionId) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.PREFIX + FormCacheNames.FORM_DETAILS + CommonCacheNames.SEPARATOR + formId;

        if (redisTemplate.hasKey(cacheKey)) {
            var formDetails = (FormResponseDto) redisTemplate.opsForValue().get(cacheKey);

            formDetails.getQuestions().removeIf(q -> q.getId().equals(questionId));

            redisTemplate.opsForValue().set(cacheKey, formDetails, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }
    }

    private void putQuestionDetails(QuestionRes question) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_DETAILS + CommonCacheNames.SEPARATOR + question.getId();

        redisTemplate.opsForValue().set(cacheKey, question, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
    }

    private void putQuestionSummary(QuestionSummaryDto question) {
        var cacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_SUMMARY + CommonCacheNames.SEPARATOR + question.getId();

        redisTemplate.opsForValue().set(cacheKey, question, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
    }


}
