package com.sougata.form_service.dto.question.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.model.Checkbox;
import com.sougata.form_service.util.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CheckboxResDto extends QuestionRes {
    private List<String> options;
    private ValidationConfig validationConfig;

    public CheckboxResDto(Long id, String question, String description, Boolean required, List<String> options, ValidationConfig validationConfig, Integer orderIndex, QuestionType questionType) {
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
                    Arrays.asList(cb.getOptions()),
                    JsonUtil.oldJsonNodeToObject(cb.getValidationConfig(), ValidationConfig.class),
                    cb.getOrderIndex(),
                    QuestionType.CHECKBOX
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(cb.getValidationConfig()));
        }
    }
}
