package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.DurationAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.DurationResDto;
import com.sougata.form_service.dto.validation.request.DurationValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Duration;
import com.sougata.form_service.repository.DurationRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DURATION_QUESTION_MANAGER")
public class DurationManager extends QuestionManager<DurationAddUpdateReqDto, DurationResDto, DurationValidationRequestDto> {

    private final DurationRepository durationRepository;
    private final FormService formService;

    public DurationManager(DurationRepository durationRepository, FormService formService) {
        this.durationRepository = durationRepository;
        this.formService = formService;
    }

    @Override
    public DurationResDto get(UUID formId, Long questionId) {
        return DurationResDto.create(durationRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public DurationResDto create(UUID formId, DurationAddUpdateReqDto crudDto) {
        Duration newDur = new Duration();

        setProperties(crudDto, formId, newDur);

        Duration saved = durationRepository.save(newDur);

        return DurationResDto.create(saved);
    }

    @Override
    public DurationResDto create(UUID formId, Long questionId, DurationAddUpdateReqDto crudDto) {
        Duration newDur = new Duration();

        newDur.setId(questionId);
        setProperties(crudDto, formId, newDur);

        Duration saved = durationRepository.save(newDur);

        return DurationResDto.create(saved);
    }

    @Override
    public DurationResDto update(Long questionId, DurationAddUpdateReqDto crudDto) {
        Duration dur = durationRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.DURATION, questionId));
        setProperties(crudDto, dur);
        durationRepository.save(dur);

        return DurationResDto.create(dur);
    }

    @Override
    public boolean exists(Long questionId) {
        return durationRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        durationRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(DurationValidationRequestDto validationDto) {
        return true;
    }

    @Override
    public Class<DurationAddUpdateReqDto> getCrudDtoClass() {
        return DurationAddUpdateReqDto.class;
    }

    @Override
    public Class<DurationValidationRequestDto> getValidationDtoClass() {
        return DurationValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DurationRepository getQuestionRepository() {
        return durationRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    private void setProperties(DurationAddUpdateReqDto source, UUID formId, Duration target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(DurationAddUpdateReqDto source, Duration target) {
        setProperties(source, null, target);
    }
}
