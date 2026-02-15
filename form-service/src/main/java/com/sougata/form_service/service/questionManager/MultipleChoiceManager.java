package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.dto.question.request.MultipleChoiceAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_service.dto.validation.request.MultipleChoiceValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.MultipleChoice;
import com.sougata.form_service.repository.MultipleChoiceRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("MULTIPLE_CHOICE_QUESTION_MANAGER")
public class MultipleChoiceManager extends QuestionManager<MultipleChoiceAddUpdateReqDto, MultipleChoiceResDto, MultipleChoiceValidationRequestDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;
    private final FormService formService;

    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, FormService formService) {
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.formService = formService;
    }

    @Override
    public MultipleChoiceResDto create(UUID formId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice newMc = new MultipleChoice();

        setProperties(crudDto, formId, newMc);

        MultipleChoice saved = multipleChoiceRepository.save(newMc);

        return MultipleChoiceResDto.create(saved);
    }

    @Override
    public MultipleChoiceResDto create(UUID formId, Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice newMc = new MultipleChoice();

        newMc.setId(questionId);
        setProperties(crudDto, formId, newMc);

        MultipleChoice saved = multipleChoiceRepository.save(newMc);

        return MultipleChoiceResDto.create(saved);
    }

    @Override
    public MultipleChoiceResDto update(Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice mc = multipleChoiceRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, questionId));
        setProperties(crudDto, mc);
        multipleChoiceRepository.save(mc);

        return MultipleChoiceResDto.create(mc);
    }

    @Override
    public boolean exists(Long questionId) {
        return multipleChoiceRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        multipleChoiceRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(MultipleChoiceValidationRequestDto validationDto) {
        var mc = multipleChoiceRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, validationDto.getQuestionId()));

        if(validationDto.getResponseIndex() >= mc.getOptions().length) {
            throw new ResponseValidationException(String.format(ValidationMessages.INVALID_MULTIPLE_CHOICE_INDEX, mc.getOptions().length - 1));
        }

        return true;
    }

    @Override
    public Class<MultipleChoiceAddUpdateReqDto> getCrudDtoClass() {
        return MultipleChoiceAddUpdateReqDto.class;
    }

    @Override
    public Class<MultipleChoiceValidationRequestDto> getValidationDtoClass() {
        return MultipleChoiceValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleChoiceRepository getQuestionRepository() {
        return multipleChoiceRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    private void setProperties(MultipleChoiceAddUpdateReqDto source, UUID formId, MultipleChoice target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOptions(source.getOptions().toArray(new String[0]));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(MultipleChoiceAddUpdateReqDto source, MultipleChoice target) {
        setProperties(source, null, target);
    }
}
