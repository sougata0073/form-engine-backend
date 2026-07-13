package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.NoQuestionRepositoryFoundException;
import com.sougata.form_service.model.questionSchema.Question;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionRepositoryFactory {

    private final ApplicationContext applicationContext;

    @Autowired
    public QuestionRepositoryFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <T extends Question, ID, QRD extends QuestionRes> QuestionRepository<T, ID, QRD>
    get(QuestionType questionType) {
        try {
            return applicationContext.getBean(
                    String.format("%s_REPOSITORY", questionType.name()),
                    QuestionRepository.class
            );
        } catch (BeansException e) {
            throw new NoQuestionRepositoryFoundException(questionType);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Question, ID, QRD extends QuestionRes> List<QuestionRepository<T, ID, QRD>> getAll() {
        List<QuestionRepository<T, ID, QRD>> repos = new ArrayList<>();

        for (QuestionType questionType : QuestionType.values()) {
            var repo = applicationContext.getBean(
                    String.format("%s_REPOSITORY", questionType.name()),
                    QuestionRepository.class
            );

            repos.add(repo);
        }

        return repos;
    }
}
