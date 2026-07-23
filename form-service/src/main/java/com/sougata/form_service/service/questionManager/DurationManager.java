package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DurationAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DurationResDto;
import com.sougata.form_service.dto.validation.request.DurationValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.DateTime;
import com.sougata.form_service.model.questionSchema.Duration;
import com.sougata.form_service.repository.DurationRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("DURATION_QUESTION_MANAGER")
public class DurationManager extends QuestionManager<Duration, DurationAddUpdateReqDto, DurationResDto, DurationValidationRequestDto> {

    private final DurationRepository durationRepository;

    public DurationManager(DurationRepository durationRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.durationRepository = durationRepository;
    }

    @Override
    public DurationResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(durationRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public DurationResDto create(UUID formId, DurationAddUpdateReqDto crudDto) {
        var newD = new Duration();

        var question = createQuestion(crudDto, formId);

        newD.setQuestion(question);

        var saved = durationRepository.save(newD);

        return toQuestionResDto(saved);
    }

    @Override
    public DurationResDto create(UUID formId, Long questionId, DurationAddUpdateReqDto crudDto) {
        var newD = new Duration();

        var question = updateQuestion(questionId, crudDto);

        newD.setQuestion(question);

        var saved = durationRepository.save(newD);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public DurationResDto update(Long questionId, DurationAddUpdateReqDto crudDto) {
        Duration dur = durationRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DURATION, questionId));

        updateQuestion(questionId, crudDto);

        durationRepository.save(dur);

        return toQuestionResDto(dur);
    }

    @Override
    public boolean validateResponse(DurationValidationRequestDto validationDto) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public void delete(Long questionId) {
        durationRepository.deleteById(questionId);
    }

    @Override
    public DurationResDto toQuestionResDto(Duration question) {
        var d = new DurationResDto();

        populateCommonFields(question, d);

        return d;
    }
}
