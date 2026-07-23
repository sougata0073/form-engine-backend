package com.sougata.form_service.service;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.dto.validation.request.ValidationRequest;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.AnyTypeQuestion;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.QuestionRepository;

import java.util.UUID;

public abstract class
QuestionManager<Q extends AnyTypeQuestion, QAUR extends QuestionAddUpdateReq, QR extends QuestionRes, V extends ValidationRequest> {

    private final QuestionRepository questionRepository;
    private final FormService formService;

    protected QuestionManager(QuestionRepository questionRepository, FormService formService) {
        this.questionRepository = questionRepository;
        this.formService = formService;
    }

    public abstract QR get(UUID formId, Long questionId);

    public abstract QR create(UUID formId, QAUR crudDto);

    public abstract QR create(UUID formId, Long questionId, QAUR crudDto);

    public abstract QR update(Long questionId, QAUR crudDto);

    public abstract boolean validateResponse(V validationDto);

    public abstract QuestionType getQuestionType();

    public abstract void delete(Long questionId);

    public boolean exists(Long questionId) {
        return questionRepository.existsById(questionId);
    }

    public abstract QR toQuestionResDto(Q questionSchema);

    public Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
    }

    public void populateCommonFields(Q question, QR questionRes) {
        questionRes.setId(question.getQuestionId());
        questionRes.setQuestion(question.getQuestion().getQuestion());
        questionRes.setQuestionType(getQuestionType());
        questionRes.setDescription(question.getQuestion().getDescription());
        questionRes.setOrderIndex(question.getQuestion().getOrderIndex());
        questionRes.setRequired(question.getQuestion().getRequired());
    }

    public Question createQuestion(QAUR source, UUID formId) {
        var newQ = new Question();

        newQ.setQuestion(source.getQuestion());
        newQ.setDescription(source.getDescription());
        newQ.setRequired(source.getRequired());
        newQ.setOrderIndex(source.getOrderIndex());
        newQ.setQuestionType(getQuestionType());
        newQ.setForm(formService.getFormById(formId));

        return questionRepository.save(newQ);
    }

    public Question updateQuestion(Long questionId, QAUR source) {
        var q = findQuestionById(questionId);

        q.setQuestion(source.getQuestion());
        q.setDescription(source.getDescription());
        q.setRequired(source.getRequired());
        q.setOrderIndex(source.getOrderIndex());
        q.setQuestionType(getQuestionType());

        return questionRepository.save(q);
    }
}
