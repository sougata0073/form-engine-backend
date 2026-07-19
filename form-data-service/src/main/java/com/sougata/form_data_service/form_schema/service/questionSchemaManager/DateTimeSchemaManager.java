package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DateTimeResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.DateTimeSchema;
import com.sougata.form_data_service.form_schema.repository.DateTimeSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("DATE_TIME_QUESTION_SCHEMA_MANAGER")
public class DateTimeSchemaManager extends QuestionSchemaManager<DateTimeSchema, DateTimeResDto, DateTimeResponseAddReqDto> {

    private final DateTimeSchemaRepository dateTimeSchemaRepository;

    public DateTimeSchemaManager(DateTimeSchemaRepository dateTimeSchemaRepository) {
        this.dateTimeSchemaRepository = dateTimeSchemaRepository;
    }

    @Override
    public DateTimeResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                dateTimeSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(DateTimeResponseAddReqDto validationDto) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public DateTimeResDto toQuestionResDto(DateTimeSchema questionSchema) {
        var dt = new DateTimeResDto();

        populateCommonFields(questionSchema, dt);

        return dt;
    }

}
