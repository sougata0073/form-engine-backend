package com.sougata.form_service.service;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.QuestionAddUpdateReq;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.AnyTypeQuestion;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.repository.QuestionRepository;

import java.util.UUID;

public abstract class
QuestionManager<Q extends AnyTypeQuestion, QAUR extends QuestionAddUpdateReq, QR extends QuestionRes> {

    private final QuestionRepository questionRepository;
    private final FormService formService;

    protected QuestionManager(QuestionRepository questionRepository, FormService formService) {
        this.questionRepository = questionRepository;
        this.formService = formService;
    }

    public abstract QR get(UUID formId, Long questionId);

    public abstract QR create(UUID formId, QAUR crudDto);

    public abstract QR create(UUID formId, Long questionId, QAUR crudDto);

    public abstract QR update(UUID formId, Long questionId, QAUR crudDto);

    public abstract QuestionType getQuestionType();

    public abstract void delete(UUID formId, Long questionId);

    public abstract QR toQuestionResDto(Q questionSchema);

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

        newQ.setForm(formService.getFormById(formId));

        newQ.setQuestion(source.getQuestion());
        newQ.setDescription(source.getDescription());
        newQ.setRequired(source.getRequired());
        newQ.setOrderIndex(questionRepository.getNextQuestionIndex(formId));
        newQ.setQuestionType(getQuestionType());

        return questionRepository.save(newQ);
    }

    public Question updateQuestion(UUID formId, Long questionId, QAUR source) {
        var q = questionRepository.findByFormIdAndId(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(getQuestionType(), questionId));

        q.setQuestion(source.getQuestion());
        q.setDescription(source.getDescription());
        q.setRequired(source.getRequired());
        q.setQuestionType(getQuestionType());

        return questionRepository.save(q);
    }
}
