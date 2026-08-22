package com.sougata.form_service.service.formSchema.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DurationAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DurationResDto;
import com.sougata.form_service.dto.template.questionTemplate.DurationTemplateDetails;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.formSchema.Duration;
import com.sougata.form_service.model.formSchema.Form;
import com.sougata.form_service.model.formSchema.Question;
import com.sougata.form_service.repository.formSchema.DurationRepository;
import com.sougata.form_service.repository.formSchema.QuestionRepository;
import com.sougata.form_service.service.formSchema.FormService;
import com.sougata.form_service.service.formSchema.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("DURATION_QUESTION_MANAGER")
public class DurationManager extends QuestionManager<Duration, DurationAddUpdateReqDto, DurationResDto, DurationTemplateDetails> {

    private final DurationRepository durationRepository;

    public DurationManager(DurationRepository durationRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.durationRepository = durationRepository;
    }

    @Override
    public DurationResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(durationRepository.findByQuestionId(questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public DurationResDto create(UUID formId, DurationAddUpdateReqDto crudDto) {
        var newD = new Duration();

        var question = createQuestion(crudDto, formId);

        newD.setQuestion(question);

        var saved = durationRepository.save(newD);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional
    public DurationResDto create(UUID formId, Long questionId, DurationAddUpdateReqDto questionAddUpdateReq) {
        var newD = new Duration();

        var question = updateQuestion(questionId, questionAddUpdateReq);

        newD.setQuestion(question);

        var saved = durationRepository.save(newD);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional
    public DurationResDto update(UUID formId, Long questionId, DurationAddUpdateReqDto questionAddUpdateReq) {
        Duration dur = durationRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DURATION, questionId));

        var question = updateQuestion(questionId, questionAddUpdateReq);

        durationRepository.save(dur);

        return toQuestionResDto(dur, question);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        durationRepository.deleteQuestion(questionId);
    }

    @Override
    public DurationResDto toQuestionResDto(Duration childQuestion) {
        return toQuestionResDto(childQuestion, childQuestion.getQuestion());
    }

    @Override
    public DurationResDto toQuestionResDto(Duration childQuestion, Question parentQuestion) {
        var d = new DurationResDto();

        populateCommonFields(parentQuestion, d);

        return d;
    }

    @Override
    public DurationAddUpdateReqDto toQuestionAddUpdateReq(DurationResDto questionRes) {
        var d = new DurationAddUpdateReqDto();

        populateCommonFields(questionRes, d);

        return d;
    }

    @Override
    @Transactional
    public Duration createFromTemplate(DurationTemplateDetails template, Form form) {
        var d = new Duration();

        d.setQuestion(createQuestionFromTemplate(template, form));

        return durationRepository.save(d);
    }
}
