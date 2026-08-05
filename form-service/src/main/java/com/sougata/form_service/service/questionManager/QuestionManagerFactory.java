package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.NoQuestionManagerFoundException;
import com.sougata.form_service.model.questionSchema.AnyTypeQuestion;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionManagerFactory {

    private final ApplicationContext applicationContext;

    public QuestionManagerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public <Q extends AnyTypeQuestion, QAUR extends QuestionAddUpdateReq, QR extends QuestionRes>
    QuestionManager<Q, QAUR, QR> get(QuestionType questionType) {
        try {
            return applicationContext.getBean(
                    String.format("%s_QUESTION_MANAGER", questionType.name()),
                    QuestionManager.class
            );
        } catch (BeansException e) {
            throw new NoQuestionManagerFoundException(questionType);
        }
    }

    @SuppressWarnings("unchecked")
    public <Q extends AnyTypeQuestion, QAUR extends QuestionAddUpdateReq, QR extends QuestionRes>
    List<QuestionManager<Q, QAUR, QR>> getAll() {

        List<QuestionManager<Q, QAUR, QR>> repos = new ArrayList<>();

        for (QuestionType questionType : QuestionType.values()) {
            var repo = applicationContext.getBean(
                    String.format("%s_QUESTION_MANAGER", questionType.name()),
                    QuestionManager.class
            );

            repos.add(repo);
        }

        return repos;
    }

}
