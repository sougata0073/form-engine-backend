package com.sougata.form_service.service.impl;

import com.sougata.form_service.dto.form.FormResponseDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.repository.AnyTypeQuestionRepositoryFactory;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormServiceCached;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FormServiceCachedImpl implements FormServiceCached {

    @Value("${app.cache.default-ttl-minutes}")
    private long cacheDefaultTtlMinutes;

    private final FormRepository formRepository;
    private final QuestionManagerFactory questionManagerFactory;
    private final AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory;
    private final QuestionRepository questionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public FormServiceCachedImpl(FormRepository formRepository, QuestionManagerFactory questionManagerFactory, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository, RedisTemplate<String, Object> redisTemplate) {
        this.formRepository = formRepository;
        this.questionManagerFactory = questionManagerFactory;
        this.anyTypeQuestionRepositoryFactory = anyTypeQuestionRepositoryFactory;
        this.questionRepository = questionRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Cacheable(cacheNames = "formDetails", key = "#id")
    @SuppressWarnings("unchecked")
    @Transactional(transactionManager = "schemaTransactionManager", readOnly = true)
    public FormResponseDto getFormDetails(UUID id) {

        System.out.println("Form details");

        Form f = formRepository.findById(id).orElseThrow(() -> new FormNotFoundException(id));

        List<QuestionRes> questionResponses = new ArrayList<>();

        var questions = questionRepository.findAllByFormId(id);
        var questionIdMap = questions.stream().collect(Collectors.toMap(
                Question::getId,
                Function.identity()
        ));

        var questionTypeMap = questions.stream().collect(Collectors.groupingBy(Question::getQuestionType));

        questionTypeMap.keySet().forEach(qType -> {
            var repo = anyTypeQuestionRepositoryFactory.get(qType);
            var manager = questionManagerFactory.get(qType);

            var qIds = questionTypeMap.get(qType).stream().map(Question::getId).collect(Collectors.toList());

            var qs = repo.findAllById((Iterable<Object>) (Iterable<?>) qIds).stream()
                    .map(q -> {
                        var parentQuestion = questionIdMap.get(q.getQuestionId());
                        return manager.toQuestionResDto(q, parentQuestion);
                    })
                    .toList();

            questionResponses.addAll(qs);
        });

        questionResponses.sort(Comparator.comparingInt(QuestionRes::getOrderIndex));

        return new FormResponseDto(
                f.getId(),
                f.getName(),
                f.getTitle(),
                f.getDescription(),
                f.getPublished(),
                f.getAcceptingResponse(),
                f.getNotAcceptingResponseMessage(),
                f.getStopAcceptingResponseOn(),
                f.getStopAcceptingResponseAfterResponse(),
                f.getLastOpenedOn(),
                questionResponses
        );
    }
}
