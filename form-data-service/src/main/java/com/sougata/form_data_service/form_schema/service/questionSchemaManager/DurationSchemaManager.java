package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DurationResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DurationResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.DurationSchema;
import com.sougata.form_data_service.form_schema.repository.DurationSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DURATION_QUESTION_SCHEMA_MANAGER")
public class DurationSchemaManager extends QuestionSchemaManager<DurationSchema, DurationResDto, DurationResponseAddReqDto> {

    private final DurationSchemaRepository durationSchemaRepository;

    public DurationSchemaManager(DurationSchemaRepository durationSchemaRepository) {
        this.durationSchemaRepository = durationSchemaRepository;
    }

    @Override
    public DurationResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                durationSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(DurationResponseAddReqDto validationDto) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public DurationResDto toQuestionResDto(DurationSchema questionSchema) {
        var d = new DurationResDto();

        populateCommonFields(questionSchema, d);

        return d;
    }

}
