package com.sougata.form_service.service.impl;

import com.sougata.form_service.constant.CommonCacheNames;
import com.sougata.form_service.constant.FormCacheNames;
import com.sougata.form_service.constant.QuestionCacheNames;
import com.sougata.form_service.dto.form.FormInfoResDto;
import com.sougata.form_service.dto.form.FormResponseDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.projection.QuestionIdProjection;
import com.sougata.form_service.repository.AnyTypeQuestionRepositoryFactory;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormServiceCached;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @SuppressWarnings("unchecked")
    @Transactional(transactionManager = "schemaTransactionManager", readOnly = true)
    public FormResponseDto getFormDetailsV2(UUID formId) {

        var formQuestionIdsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_QUESTION_IDS + CommonCacheNames.SEPARATOR + formId;

        List<Long> cacheQuestionIds = null;
        List<Long> dbQuestionIds = null;

        if (redisTemplate.hasKey(formQuestionIdsCacheKey)) {
            cacheQuestionIds = redisTemplate.opsForList().range(formQuestionIdsCacheKey, 0, -1).stream().map(id -> (Long) id).toList();
        } else {
            dbQuestionIds = questionRepository.findQuestionIdsByFormId(formId).stream().map(QuestionIdProjection::getId).toList();
        }

        var questionIds = cacheQuestionIds == null ? dbQuestionIds : cacheQuestionIds;

        var questionDetailsCacheKeys = questionIds.stream().map(questionId ->
            CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_DETAILS + CommonCacheNames.SEPARATOR + questionId
        ).toList();

        var cacheQuestionDetailsList = redisTemplate.opsForValue().multiGet(questionDetailsCacheKeys);

        List<QuestionRes> questionResponses = new ArrayList<>(
                cacheQuestionDetailsList.stream()
                        .filter(Objects::nonNull)
                        .map(questionDetails -> (QuestionRes) questionDetails)
                        .toList()
        );

        var cacheMissQuestionIds = new ArrayList<Long>();

        for (int i = 0; i < cacheQuestionDetailsList.size(); i++) {
            if (cacheQuestionDetailsList.get(i) == null) {
                cacheMissQuestionIds.add(questionIds.get(i));
            }
        }

        var dbQuestions = questionRepository.findAllById(cacheMissQuestionIds);
        var dbQuestionIdMap = dbQuestions.stream().collect(Collectors.toMap(
                Question::getId,
                Function.identity()
        ));
        var dbQuestionTypeMap = dbQuestions.stream().collect(Collectors.groupingBy(Question::getQuestionType));

        var cacheableQuestionDetailsList = new ArrayList<QuestionRes>();

        dbQuestionTypeMap.keySet().forEach(qType -> {
            var repo = anyTypeQuestionRepositoryFactory.get(qType);
            var manager = questionManagerFactory.get(qType);

            var qIds = dbQuestionTypeMap.get(qType).stream().map(Question::getId).collect(Collectors.toList());

            var qs = repo.findAllById((Iterable<Object>) (Iterable<?>) qIds).stream()
                    .map(q -> {
                        var parentQuestion = dbQuestionIdMap.get(q.getQuestionId());
                        return manager.toQuestionResDto(q, parentQuestion);
                    })
                    .toList();

            cacheableQuestionDetailsList.addAll(qs);
            questionResponses.addAll(qs);
        });


        var keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        var valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<?>) connection -> {

            cacheableQuestionDetailsList.forEach(questionDetails -> {

                var questionDetailsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + QuestionCacheNames.QUESTION_DETAILS + CommonCacheNames.SEPARATOR + questionDetails.getId();

                byte[] key = keySerializer.serialize(questionDetailsCacheKey);
                byte[] value = valueSerializer.serialize(questionDetails);

                connection.stringCommands().set(
                        key,
                        value,
                        Expiration.seconds(cacheDefaultTtlMinutes * 60),
                        RedisStringCommands.SetOption.UPSERT
                );
            });

            return null;
        });

        var formInfoCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_INFO + CommonCacheNames.SEPARATOR + formId;

        FormInfoResDto formInfo;

        if (redisTemplate.hasKey(formInfoCacheKey)) {
            formInfo = (FormInfoResDto) redisTemplate.opsForValue().get(formInfoCacheKey);
        } else {
            Form form = formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(formId));
            formInfo = FormInfoResDto.create(form);
        }

        return new FormResponseDto(
                formInfo.getId(),
                formInfo.getName(),
                formInfo.getTitle(),
                formInfo.getDescription(),
                formInfo.getPublished(),
                formInfo.getAcceptingResponse(),
                formInfo.getNotAcceptingResponseMessage(),
                formInfo.getStopAcceptingResponseOn(),
                formInfo.getStopAcceptingResponseAfterResponse(),
                formInfo.getLastOpenedOn(),
                questionResponses
        );
    }

}
