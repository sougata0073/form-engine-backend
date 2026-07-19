package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DropdownResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DropdownResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.DropdownSchema;
import com.sougata.form_data_service.form_schema.repository.DropdownSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service("DROPDOWN_QUESTION_SCHEMA_MANAGER")
public class DropdownSchemaManager extends QuestionSchemaManager<DropdownSchema, DropdownResDto, DropdownResponseAddReqDto> {

    private final DropdownSchemaRepository dropdownSchemaRepository;

    public DropdownSchemaManager(DropdownSchemaRepository dropdownSchemaRepository) {
        this.dropdownSchemaRepository = dropdownSchemaRepository;
    }

    @Override
    public DropdownResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                dropdownSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(DropdownResponseAddReqDto validationDto) {
        DropdownSchema dd = dropdownSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.DROPDOWN, validationDto.getQuestionId()));

        var present = dd.getOptions()
                .stream().anyMatch(op -> Objects.equals(op.getId(), validationDto.getResponseOptionId()));

        if (!present) {
            throw new ResponseValidationException(
                    "Invalid dropdown option ID: " + validationDto.getResponseOptionId()
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    @Override
    public DropdownResDto toQuestionResDto(DropdownSchema questionSchema) {
        var dd = new DropdownResDto();

        populateCommonFields(questionSchema, dd);

        dd.setOptions(
                questionSchema.getOptions().stream()
                        .map(o -> new DropdownResDto.DropdownOptionResDto(o.getId(), o.getOption(), o.getOrderIndex()))
                        .toList()
        );

        return dd;
    }

}
