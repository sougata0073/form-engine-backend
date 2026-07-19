package com.sougata.form_data_service.form_schema.service;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionRes;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionSummariesResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.projection.QuestionSchemaSummaryProjection;
import com.sougata.form_data_service.form_schema.repository.QuestionSchemaRepositoryFactory;
import com.sougata.form_data_service.form_schema.service.questionSchemaManager.QuestionSchemaManagerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

@Service
@Transactional(transactionManager = "formSchemaTransactionManager")
public class QuestionSchemaService {

    private final QuestionSchemaRepositoryFactory questionSchemaRepositoryFactory;
    private final QuestionSchemaManagerFactory questionSchemaManagerFactory;

    public QuestionSchemaService(QuestionSchemaRepositoryFactory questionSchemaRepositoryFactory, QuestionSchemaManagerFactory questionSchemaManagerFactory) {
        this.questionSchemaRepositoryFactory = questionSchemaRepositoryFactory;
        this.questionSchemaManagerFactory = questionSchemaManagerFactory;
    }

    public QuestionRes getQuestion(UUID formId, Long questionId) {
        QuestionRes questionRes = null;

        for (var repo : questionSchemaManagerFactory.getAll()) {
            try {
                questionRes = repo.get(formId, questionId);
                break;
            } catch (QuestionSchemaNotFoundException ignored) {
            }
        }

        if (questionRes == null) {
            throw new QuestionSchemaNotFoundException(questionId);
        }

        return questionRes;
    }

    public QuestionSummariesResDto getQuestionSummaries(UUID formId) {
        var questionProjections = new ArrayList<QuestionSchemaSummaryProjection>();
        var questionTypeMap = new HashMap<Long, QuestionType>();

        questionSchemaRepositoryFactory.getAll().forEach(repo -> {
            var ques = repo.findQuestionSummariesByFormId(formId);

            ques.forEach(q -> questionTypeMap.put(q.getId(), repo.getQuestionType()));

            questionProjections.addAll(ques);
        });

        questionProjections.sort(Comparator.comparingInt(QuestionSchemaSummaryProjection::getOrderIndex));

        var questions = questionProjections
                .stream()
                .map(q ->
                        new QuestionSummaryDto(q.getId(), q.getQuestion(), questionTypeMap.get(q.getId()), q.getOrderIndex())
                ).toList();

        return new QuestionSummariesResDto(questions);
    }

    public QuestionSummaryDto getQuestionSummary(UUID formId, Long questionId) {
        QuestionSummaryDto summary = null;

        for (var repo : questionSchemaRepositoryFactory.getAll()) {
            var qOptional = repo.findQuestionSummaryByFormIdAndId(formId, questionId);

            if (qOptional.isPresent()) {
                var q = qOptional.get();
                summary = new QuestionSummaryDto(q.getId(), q.getQuestion(), repo.getQuestionType(), q.getOrderIndex());

                break;
            }
        }

        if (summary == null) {
            throw new QuestionSchemaNotFoundException(questionId);
        }

        return summary;
    }

}
