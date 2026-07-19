package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TimeResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TimeResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.TimeSchema;
import com.sougata.form_data_service.form_schema.repository.TimeSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("TIME_QUESTION_SCHEMA_MANAGER")
public class TimeSchemaManager extends QuestionSchemaManager<TimeSchema, TimeResDto, TimeResponseAddReqDto> {

    private final TimeSchemaRepository timeSchemaRepository;

    public TimeSchemaManager(TimeSchemaRepository timeSchemaRepository) {
        this.timeSchemaRepository = timeSchemaRepository;
    }

    @Override
    public TimeResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                timeSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(TimeResponseAddReqDto validationDto) {
        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    public TimeResDto toQuestionResDto(TimeSchema questionSchema) {
        var t = new TimeResDto();

        populateCommonFields(questionSchema, t);

        return t;
    }

}
