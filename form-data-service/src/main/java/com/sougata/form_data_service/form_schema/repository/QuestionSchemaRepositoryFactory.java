package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionRes;
import com.sougata.form_data_service.form_schema.model.QuestionSchema;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionSchemaRepositoryFactory {

    private final ApplicationContext applicationContext;

    @Autowired
    public QuestionSchemaRepositoryFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestionSchema, ID, QRD extends QuestionRes> QuestionSchemaRepository<T, ID, QRD>
    get(QuestionType questionType) {
        try {
            return applicationContext.getBean(
                    String.format("%s_SCHEMA_REPOSITORY", questionType.name()),
                    QuestionSchemaRepository.class
            );
        } catch (BeansException e) {
            throw new IllegalArgumentException("No question manager found for question type: " + questionType);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestionSchema, ID, QRD extends QuestionRes> List<QuestionSchemaRepository<T, ID, QRD>> getAll() {
        List<QuestionSchemaRepository<T, ID, QRD>> repos = new ArrayList<>();

        for (QuestionType questionType : QuestionType.values()) {
            var repo = applicationContext.getBean(
                    String.format("%s_SCHEMA_REPOSITORY", questionType.name()),
                    QuestionSchemaRepository.class
            );

            repos.add(repo);
        }

        return repos;
    }
}
