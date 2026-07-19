package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.ExceptionMessages;
import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.LinearScaleResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.LinearScaleResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.LinearScaleSchema;
import com.sougata.form_data_service.form_schema.repository.LinearScaleSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("LINEAR_SCALE_QUESTION_SCHEMA_MANAGER")
public class LinearScaleSchemaManager extends QuestionSchemaManager<LinearScaleSchema, LinearScaleResDto, LinearScaleResponseAddReqDto> {

    private final LinearScaleSchemaRepository linearScaleSchemaRepository;

    public LinearScaleSchemaManager(LinearScaleSchemaRepository linearScaleSchemaRepository) {
        this.linearScaleSchemaRepository = linearScaleSchemaRepository;
    }

    @Override
    public LinearScaleResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                linearScaleSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(LinearScaleResponseAddReqDto validationDto) {
        Integer toNumber = linearScaleSchemaRepository.getToNumber(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.LINEAR_SCALE, validationDto.getQuestionId()));

        if (validationDto.getScale() > toNumber) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_SCALE, toNumber, validationDto.getScale()
                    )
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    public LinearScaleResDto toQuestionResDto(LinearScaleSchema questionSchema) {
        var ls = new LinearScaleResDto();

        populateCommonFields(questionSchema, ls);

        ls.setFromNumber(questionSchema.getFromNumber());
        ls.setToNumber(questionSchema.getToNumber());

        return ls;
    }

}
