package com.sougata.form_service.dto.question.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.util.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CheckboxResDto extends QuestionRes {
    private List<CheckboxOptionResDto> options;
    private ValidationConfig validationConfig;

    public record CheckboxOptionResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String option,
            Integer orderIndex
    ) {
    }
}
