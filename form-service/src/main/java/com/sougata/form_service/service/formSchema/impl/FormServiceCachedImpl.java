package com.sougata.form_service.service.formSchema.impl;

import com.sougata.form_service.dto.form.FormDetailsDto;
import com.sougata.form_service.dto.question.response.QuestionDetails;
import com.sougata.form_service.exception.FormNotFoundException;
import com.sougata.form_service.model.formSchema.Form;
import com.sougata.form_service.model.formSchema.Question;
import com.sougata.form_service.repository.formSchema.AnyTypeQuestionRepositoryFactory;
import com.sougata.form_service.repository.formSchema.FormRepository;
import com.sougata.form_service.repository.formSchema.QuestionRepository;
import com.sougata.form_service.service.formSchema.FormServiceCached;
import com.sougata.form_service.service.formSchema.questionManager.QuestionManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

    private final FormRepository formRepository;
    private final QuestionManagerFactory questionManagerFactory;
    private final AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory;
    private final QuestionRepository questionRepository;

    @Autowired
    @Lazy
    private FormServiceCached self;

    public FormServiceCachedImpl(FormRepository formRepository, QuestionManagerFactory questionManagerFactory, AnyTypeQuestionRepositoryFactory anyTypeQuestionRepositoryFactory, QuestionRepository questionRepository) {
        this.formRepository = formRepository;
        this.questionManagerFactory = questionManagerFactory;
        this.anyTypeQuestionRepositoryFactory = anyTypeQuestionRepositoryFactory;
        this.questionRepository = questionRepository;
    }

    @Override
    @Cacheable(cacheNames = {"formDetails"}, key = "#formId", sync = true)
    public FormDetailsDto getFormDetails(UUID formId) {
        return self.loadFormDetailsFromDb(formId);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public FormDetailsDto loadFormDetailsFromDb(UUID formId) {

        Form f = formRepository.findById(formId).orElseThrow(() -> new FormNotFoundException(formId));

        List<QuestionDetails> questionResponses = new ArrayList<>();

        var questions = questionRepository.findAllByFormId(formId);
        var questionIdMap = questions.stream().collect(Collectors.toMap(
                Question::getId,
                Function.identity()
        ));

        var questionTypeMap = questions.stream().collect(Collectors.groupingBy(Question::getQuestionType));

        questionTypeMap.forEach((qType, qs) -> {
            var repo = anyTypeQuestionRepositoryFactory.get(qType);
            var manager = questionManagerFactory.get(qType);

            var qIds = qs.stream().map(Question::getId).collect(Collectors.toList());

            var qResList = repo.findAllById((Iterable<Object>) (Iterable<?>) qIds).stream()
                    .map(q -> {
                        var parentQuestion = questionIdMap.get(q.getQuestionId());
                        return manager.toQuestionResDto(q, parentQuestion);
                    })
                    .toList();

            questionResponses.addAll(qResList);
        });

        questionResponses.sort(Comparator.comparingInt(QuestionDetails::getOrderIndex));

        return new FormDetailsDto(
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
