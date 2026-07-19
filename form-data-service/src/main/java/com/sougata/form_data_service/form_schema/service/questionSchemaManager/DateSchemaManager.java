package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DateResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.DateSchema;
import com.sougata.form_data_service.form_schema.repository.DateSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DATE_QUESTION_SCHEMA_MANAGER")
public class DateSchemaManager extends QuestionSchemaManager<DateSchema, DateResDto, DateResponseAddReqDto> {

    private final DateSchemaRepository dateSchemaRepository;

    public DateSchemaManager(DateSchemaRepository dateSchemaRepository) {
        this.dateSchemaRepository = dateSchemaRepository;
    }

    @Override
    public DateResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                dateSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(DateResponseAddReqDto validationDto) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public DateResDto toQuestionResDto(DateSchema questionSchema) {
        var d = new DateResDto();

        populateCommonFields(questionSchema, d);

        return d;
    }
}
