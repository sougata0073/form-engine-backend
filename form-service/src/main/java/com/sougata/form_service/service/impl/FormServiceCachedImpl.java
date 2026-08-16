package com.sougata.form_service.service.impl;

import com.sougata.form_service.configuration.AppConfiguration;
import com.sougata.form_service.constant.CommonCacheNames;
import com.sougata.form_service.constant.cacheNames.FormCacheNames;
import com.sougata.form_service.constant.cacheNames.QuestionCacheNames;
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
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final RedisTemplate<String, String> redisTemplateString;
    private final AppConfiguration appConfiguration;

    public FormServiceCachedImpl(FormRepository formRepository, QuestionManagerFactory questionManagerFactory, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository, RedisTemplate<String, Object> redisTemplate, RedisTemplate<String, String> redisTemplateString, AppConfiguration appConfiguration) {
        this.formRepository = formRepository;
        this.questionManagerFactory = questionManagerFactory;
        this.anyTypeQuestionRepositoryFactory = anyTypeQuestionRepositoryFactory;
        this.questionRepository = questionRepository;
        this.redisTemplate = redisTemplate;
        this.redisTemplateString = redisTemplateString;
        this.appConfiguration = appConfiguration;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(transactionManager = "schemaTransactionManager", readOnly = true)
    public FormResponseDto getFormDetails(UUID formId) {
        var formQuestionIdsCacheKey = CommonCacheNames.PREFIX + CommonCacheNames.SEPARATOR + FormCacheNames.FORM_QUESTION_IDS + CommonCacheNames.SEPARATOR + formId;

        List<Long> cacheQuestionIds = null;
        List<Long> dbQuestionIds = null;

        if (redisTemplate.hasKey(formQuestionIdsCacheKey)) {
            cacheQuestionIds = redisTemplateString.opsForSet().members(formQuestionIdsCacheKey).stream().map(Long::parseLong).toList();
        } else {
            dbQuestionIds = questionRepository.findQuestionIdsByFormId(formId).stream().map(QuestionIdProjection::getId).toList();
        }

        var questionIds = cacheQuestionIds == null ? dbQuestionIds : cacheQuestionIds;

        if (dbQuestionIds != null && !dbQuestionIds.isEmpty()) {
            var cacheableQuestionIds = dbQuestionIds.stream().map(String::valueOf).toArray(String[]::new);
            redisTemplateString.opsForSet().add(formQuestionIdsCacheKey, cacheableQuestionIds);
        }
        redisTemplateString.expire(formQuestionIdsCacheKey, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));

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
            redisTemplate.opsForValue().set(formInfoCacheKey, formInfo, Duration.ofMinutes(appConfiguration.getCacheDefaultTtlMinutes()));
        }

        questionResponses.sort(Comparator.comparingInt(QuestionRes::getOrderIndex));

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
