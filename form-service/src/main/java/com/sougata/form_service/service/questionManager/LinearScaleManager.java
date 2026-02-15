package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.LinearScaleAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_service.dto.validation.request.LinearScaleValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.LinearScale;
import com.sougata.form_service.repository.LinearScaleRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("LINEAR_SCALE_QUESTION_MANAGER")
public class LinearScaleManager extends QuestionManager<LinearScaleAddUpdateReqDto, LinearScaleResDto, LinearScaleValidationRequestDto> {

    private final LinearScaleRepository linearScaleRepository;
    private final FormService formService;

    public LinearScaleManager(LinearScaleRepository linearScaleRepository, FormService formService) {
        this.linearScaleRepository = linearScaleRepository;
        this.formService = formService;
    }

    @Override
    public LinearScaleResDto create(UUID formId, LinearScaleAddUpdateReqDto crudDto) {
        LinearScale newLs = new LinearScale();

        setProperties(crudDto, formId, newLs);

        LinearScale saved = linearScaleRepository.save(newLs);

        return LinearScaleResDto.create(saved);
    }

    @Override
    public LinearScaleResDto create(UUID formId, Long questionId, LinearScaleAddUpdateReqDto crudDto) {
        LinearScale newLs = new LinearScale();

        newLs.setId(questionId);
        setProperties(crudDto, formId, newLs);

        LinearScale saved = linearScaleRepository.save(newLs);

        return LinearScaleResDto.create(saved);
    }

    @Override
    public LinearScaleResDto update(Long questionId, LinearScaleAddUpdateReqDto crudDto) {
        LinearScale ls = linearScaleRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.LINEAR_SCALE, questionId));
        setProperties(crudDto, ls);
        linearScaleRepository.save(ls);

        return LinearScaleResDto.create(ls);
    }

    @Override
    public boolean exists(Long questionId) {
        return linearScaleRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        linearScaleRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(LinearScaleValidationRequestDto validationDto) {
        LinearScale ls = linearScaleRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.LINEAR_SCALE, validationDto.getQuestionId()));

        if(validationDto.getScale() > ls.getToNumber()) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_SCALE, ls.getToNumber(), validationDto.getScale()
                    )
            );
        }

        return true;
    }

    @Override
    public Class<LinearScaleAddUpdateReqDto> getCrudDtoClass() {
        return LinearScaleAddUpdateReqDto.class;
    }

    @Override
    public Class<LinearScaleValidationRequestDto> getValidationDtoClass() {
        return LinearScaleValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinearScaleRepository getQuestionRepository() {
        return linearScaleRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    private void setProperties(LinearScaleAddUpdateReqDto source, UUID formId, LinearScale target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setFromNumber(source.getFromNumber());
        target.setToNumber(source.getToNumber());
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(LinearScaleAddUpdateReqDto source, LinearScale target) {
        setProperties(source, null, target);
    }
}
