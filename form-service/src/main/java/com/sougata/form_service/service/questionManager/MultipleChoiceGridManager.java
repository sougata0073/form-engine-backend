package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.dto.question.request.MultipleChoiceGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_service.dto.validation.request.MultipleChoiceGridValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.MultipleChoiceGrid;
import com.sougata.form_service.repository.MultipleChoiceGridRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_MANAGER")
public class MultipleChoiceGridManager extends QuestionManager<MultipleChoiceGridAddUpdateReqDto, MultipleChoiceGridResDto, MultipleChoiceGridValidationRequestDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;
    private final FormService formService;

    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository, FormService formService) {
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
        this.formService = formService;
    }

    @Override
    public MultipleChoiceGridResDto create(UUID formId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid newMcg = new MultipleChoiceGrid();

        setProperties(crudDto, formId, newMcg);

        MultipleChoiceGrid saved = multipleChoiceGridRepository.save(newMcg);

        return MultipleChoiceGridResDto.create(saved);
    }

    @Override
    public MultipleChoiceGridResDto create(UUID formId, Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid newMcg = new MultipleChoiceGrid();

        newMcg.setId(questionId);
        setProperties(crudDto, formId, newMcg);

        MultipleChoiceGrid saved = multipleChoiceGridRepository.save(newMcg);

        return MultipleChoiceGridResDto.create(saved);
    }

    @Override
    public MultipleChoiceGridResDto update(Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid mcg = multipleChoiceGridRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, questionId));
        setProperties(crudDto, mcg);
        multipleChoiceGridRepository.save(mcg);

        return MultipleChoiceGridResDto.create(mcg);
    }

    @Override
    public boolean exists(Long questionId) {
        return multipleChoiceGridRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        multipleChoiceGridRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(MultipleChoiceGridValidationRequestDto validationDto) {
        var mcg = multipleChoiceGridRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, validationDto.getQuestionId()));

        if (mcg.getEachRowRequired() && validationDto.getRows().size() != mcg.getRows().length) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_MULTIPLE_CHOICE_GRID_ROW_LENGTH, mcg.getRows().length, validationDto.getRows().size()
                    )
            );
        }

        validationDto.getRows().forEach(row -> {
            if (row.responseIndex() >= mcg.getColumns().length) {
                throw new ResponseValidationException(
                        String.format(
                                ValidationMessages.INVALID_MULTIPLE_CHOICE_GRID_COLUMN_RANGE, row.responseIndex()
                        )
                );
            }
        });

        return true;

    }

    @Override
    public Class<MultipleChoiceGridAddUpdateReqDto> getCrudDtoClass() {
        return MultipleChoiceGridAddUpdateReqDto.class;
    }

    @Override
    public Class<MultipleChoiceGridValidationRequestDto> getValidationDtoClass() {
        return MultipleChoiceGridValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleChoiceGridRepository getQuestionRepository() {
        return multipleChoiceGridRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    private void setProperties(MultipleChoiceGridAddUpdateReqDto source, UUID formId, MultipleChoiceGrid target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setEachRowRequired(source.getEachRowRequired());
        target.setRows(source.getRows().toArray(new String[0]));
        target.setColumns(source.getColumns().toArray(new String[0]));
        target.setOrderIndex(source.getOrderIndex());
        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(MultipleChoiceGridAddUpdateReqDto source, MultipleChoiceGrid target) {
        setProperties(source, null, target);
    }
}
