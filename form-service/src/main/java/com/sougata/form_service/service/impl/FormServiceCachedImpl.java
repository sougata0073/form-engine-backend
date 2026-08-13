package com.sougata.form_service.service.impl;

import com.sougata.form_service.dto.form.FormResponseDto;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.projection.QuestionSummaryProjection;
import com.sougata.form_service.repository.AnyTypeQuestionRepositoryFactory;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormServiceCached;
import com.sougata.form_service.service.questionManager.QuestionManagerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FormServiceCachedImpl implements FormServiceCached {

    private final FormRepository formRepository;
    private final QuestionManagerFactory questionManagerFactory;
    private final AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory;
    private final QuestionRepository questionRepository;

    public FormServiceCachedImpl(FormRepository formRepository, QuestionManagerFactory questionManagerFactory, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository) {
        this.formRepository = formRepository;
        this.questionManagerFactory = questionManagerFactory;
        this.anyTypeQuestionRepositoryFactory = anyTypeQuestionRepositoryFactory;
        this.questionRepository = questionRepository;
    }

    @Override
    @Cacheable(cacheNames = "formDetails", key = "#id")
    @SuppressWarnings("unchecked")
    public FormResponseDto getFormDetails(UUID id) {
        Form f = formRepository.findById(id).orElseThrow(() -> new FormNotFoundException(id));

        List<QuestionRes> questionResponses = new ArrayList<>();

        var questions = questionRepository.findQuestionSummariesByFormId(id);
        var questionTypeMap = questions.stream().collect(Collectors.groupingBy(QuestionSummaryProjection::getQuestionType));

        questionTypeMap.keySet().forEach(qType -> {
            var repo = anyTypeQuestionRepositoryFactory.get(qType);
            var manager = questionManagerFactory.get(qType);
            var qIds = questionTypeMap.get(qType).stream().map(QuestionSummaryProjection::getId).collect(Collectors.toList());

            var qs = repo.findAllById((Iterable<Object>) (Iterable<?>) qIds).stream().map(manager::toQuestionResDto).toList();

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
