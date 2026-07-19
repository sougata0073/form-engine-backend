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

    public CheckboxResDto(Long id, String question, String description, Boolean required, List<CheckboxOptionResDto> options, ValidationConfig validationConfig, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
        this.options = options;
        this.validationConfig = validationConfig;
    }

    public static CheckboxResDto create(Checkbox cb) {
        try {
            return new CheckboxResDto(
                    cb.getId(),
                    cb.getQuestion(),
                    cb.getDescription(),
                    cb.getRequired(),
                    cb.getOptions().stream().map(o -> new CheckboxOptionResDto(o.getId(), o.getOption(), o.getOrderIndex())).toList(),
                    JsonUtil.oldJsonNodeToObject(cb.getValidationConfig(), ValidationConfig.class),
                    cb.getOrderIndex(),
                    QuestionType.CHECKBOX
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(cb.getValidationConfig()));
        }
    }

    public record CheckboxOptionResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String option,
            Integer orderIndex
    ) {
    }
}
