package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.MultipleChoiceResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceSchema;
import com.sougata.form_data_service.form_schema.repository.MultipleChoiceSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service("MULTIPLE_CHOICE_QUESTION_SCHEMA_MANAGER")
public class MultipleChoiceSchemaManager extends QuestionSchemaManager<MultipleChoiceSchema, MultipleChoiceResDto, MultipleChoiceResponseAddReqDto> {

    private final MultipleChoiceSchemaRepository multipleChoiceSchemaRepository;

    public MultipleChoiceSchemaManager(MultipleChoiceSchemaRepository multipleChoiceSchemaRepository) {
        this.multipleChoiceSchemaRepository = multipleChoiceSchemaRepository;
    }

    @Override
    public MultipleChoiceResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                multipleChoiceSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(MultipleChoiceResponseAddReqDto validationDto) {
        var mc = multipleChoiceSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.MULTIPLE_CHOICE, validationDto.getQuestionId()));

        var present = mc.getOptions()
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
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public MultipleChoiceResDto toQuestionResDto(MultipleChoiceSchema questionSchema) {
        var m = new MultipleChoiceResDto();

        populateCommonFields(questionSchema, m);

        m.setOptions(
                questionSchema.getOptions().stream()
                        .map(op ->
                                new MultipleChoiceResDto.MultipleChoiceOptionResDto(op.getId(), op.getOption(), op.getOrderIndex())
                        )
                        .toList()
        );

        return m;
    }

}
