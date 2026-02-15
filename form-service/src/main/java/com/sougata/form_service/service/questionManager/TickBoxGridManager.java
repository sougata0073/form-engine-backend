package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.dto.question.request.TickBoxGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_service.dto.validation.request.TickBoxGridValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.TickBoxGrid;
import com.sougata.form_service.repository.TickBoxGridRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("TICK_BOX_GRID_QUESTION_MANAGER")
public class TickBoxGridManager extends QuestionManager<TickBoxGridAddUpdateReqDto, TickBoxGridResDto, TickBoxGridValidationRequestDto> {

    private final TickBoxGridRepository tickBoxGridRepository;
    private final FormService formService;

    public TickBoxGridManager(TickBoxGridRepository tickBoxGridRepository, FormService formService) {
        this.tickBoxGridRepository = tickBoxGridRepository;
        this.formService = formService;
    }

    @Override
    public TickBoxGridResDto create(UUID formId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid newTbg = new TickBoxGrid();

        setProperties(crudDto, formId, newTbg);

        TickBoxGrid saved = tickBoxGridRepository.save(newTbg);

        return TickBoxGridResDto.create(saved);
    }

    @Override
    public TickBoxGridResDto create(UUID formId, Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid newTbg = new TickBoxGrid();

        newTbg.setId(questionId);
        setProperties(crudDto, formId, newTbg);

        TickBoxGrid saved = tickBoxGridRepository.save(newTbg);

        return TickBoxGridResDto.create(saved);
    }

    @Override
    public TickBoxGridResDto update(Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid tbg = tickBoxGridRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TICK_BOX_GRID, questionId));
        setProperties(crudDto, tbg);
        tickBoxGridRepository.save(tbg);

        return TickBoxGridResDto.create(tbg);
    }

    @Override
    public boolean exists(Long questionId) {
        return tickBoxGridRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        tickBoxGridRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(TickBoxGridValidationRequestDto validationDto) {
        var tbg = tickBoxGridRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TICK_BOX_GRID, validationDto.getQuestionId()));

        if(tbg.getEachRowRequired() && validationDto.getRows().size() != tbg.getRows().length) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_TICK_BOX_GRID_ROW_LENGTH, tbg.getRows().length, validationDto.getRows().size()
                    )
            );
        }

        validationDto.getRows().forEach(row -> {
            row.responseIndexes().forEach(r -> {
                if (r >= tbg.getColumns().length) {
                    throw new ResponseValidationException(
                            String.format(
                                    ValidationMessages.INVALID_TICK_BOX_GRID_COLUMN_RANGE, r
                            )
                    );
                }
            });
        });

        return true;
    }

    @Override
    public Class<TickBoxGridAddUpdateReqDto> getCrudDtoClass() {
        return TickBoxGridAddUpdateReqDto.class;
    }

    @Override
    public Class<TickBoxGridValidationRequestDto> getValidationDtoClass() {
        return TickBoxGridValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TickBoxGridRepository getQuestionRepository() {
        return tickBoxGridRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    private void setProperties(TickBoxGridAddUpdateReqDto source, UUID formId, TickBoxGrid target) {
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

    private void setProperties(TickBoxGridAddUpdateReqDto source, TickBoxGrid target) {
        setProperties(source, null, target);
    }
}
