package com.sougata.form_service.service.formSchema.impl;

import com.sougata.form_service.constant.cacheNames.FormCacheNames;
import com.sougata.form_service.dto.form.FormDetailsDto;
import com.sougata.form_service.dto.question.response.QuestionDetails;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.model.formSchema.Form;
import com.sougata.form_service.model.formSchema.Question;
import com.sougata.form_service.repository.formSchema.FormRepository;
import com.sougata.form_service.repository.formSchema.QuestionRepository;
import com.sougata.form_service.service.formSchema.FormServiceCached;
import com.sougata.form_service.service.formSchema.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormServiceCachedImpl implements FormServiceCached {

    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    @Autowired
    @Lazy
    private FormServiceCached self;

    @Override
    @Cacheable(cacheNames = {FormCacheNames.FORM_DETAILS}, key = "#formId", sync = true)
    public FormDetailsDto getFormDetails(UUID formId) {
        return self.loadFormDetailsFromDb(formId);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDetailsDto loadFormDetailsFromDb(UUID formId) {

        Form form = formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(formId));

        var parentQuestions = questionRepository.findAllByFormId(formId);

        var questionTypeMap = parentQuestions.stream().collect(Collectors.groupingBy(Question::getQuestionType));

        var questionsFutures = new ArrayList<CompletableFuture<List<QuestionDetails>>>();

        questionTypeMap.forEach((qType, pQuestions) -> {
            var questionsFuture = CompletableFuture.supplyAsync(() ->
                    questionService.getSimilarTypeQuestions(qType, pQuestions)
            );

            questionsFutures.add(questionsFuture);
        });

        List<QuestionDetails> questionDetailsList = CompletableFuture.allOf(questionsFutures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        questionsFutures.stream()
                                .flatMap(f -> f.join().stream())
                                .sorted(Comparator.comparingInt(QuestionDetails::getOrderIndex))
                                .toList()
                ).join();

        return new FormDetailsDto(
                form.getId(),
                form.getName(),
                form.getTitle(),
                form.getDescription(),
                form.getPublished(),
                form.getAcceptingResponse(),
                form.getNotAcceptingResponseMessage(),
                form.getStopAcceptingResponseOn(),
                form.getStopAcceptingResponseAfterResponse(),
                form.getLastOpenedOn(),
                questionDetailsList
        );
    }
}
